import sys
from pymongo import MongoClient  # Mongo database
import config  # Get config settings from credentials file

# The following imports are for creating unique tokens for each msg
import binascii
import os

####
# App globals:
###
CONFIG = config.configuration()

MONGO_CLIENT_URL = "mongodb://{}:{}@{}:{}/{}".format(
    CONFIG.DB_USER,
    CONFIG.DB_USER_PW,
    CONFIG.DB_HOST,
    CONFIG.DB_PORT,
    CONFIG.DB)

if CONFIG.DEBUG is True:
    print("Using URL '{}'".format(MONGO_CLIENT_URL))

###
# Database connection per server process:
###
try:
    dbclient = MongoClient(MONGO_CLIENT_URL)
    db = getattr(dbclient, str(CONFIG.DB))
    users = db.users
    messages = db.messages
except:
    print("Failure opening database. Is Mongo running? Correct password?")
    sys.exit(1)


###
# Mongo msg Functions
###

def mongo_login(acc):
    """
    Reference: http://api.mongodb.com/python/current/api/pymongo/users.html
    """
    record = users.find_one({"username": acc["username"]})
    result = {'username': '',
              'date_created': '',
              'friends': [],
              'user_id': '',
              'messages': [],
              'error_msg': ''}

    if record is None:
        result['error_msg'] = "user not found"
        return result, result['error_msg']

    if record['password'] != acc['password']:
        result['error_msg'] = "password mismatch"
        return result, result['error_msg']

    result['username'] = record['username']
    result['date_created'] = record['date_created']
    result['friends'] = record['friends']
    result['user_id'] = record['user_id']
    result['messages'] = mongo_getMsgs(record['user_id'])
    return result, ""


def mongo_signup(acc):
    """
    Reference: http://api.mongodb.com/python/current/api/pymongo/users.html
    """
    record = users.find_one({"username": acc["username"]})
    result = {'username': '',
              'user_id': '',
              'date_created': '',
              'friends': [],
              'error_msg': ''}

    # if user already exists,
    if record is not None:
        result['error_msg'] = "user already exists"
        return result, result['error_msg']

    user_id = generate_key()
    new_acc = {
        'username': acc['username'],
        'password': acc['password'],
        'date_created': acc['date_created'],
        'friends': [],
        'user_id': user_id
    }
    users.insert_one(new_acc)

    result['username'] = acc['username']
    result['user_id'] = user_id
    result['date_created'] = acc['date_created']
    return result, ""


def mongo_addMsg(username, msg):
    """
    Reference: http://api.mongodb.com/python/current/api/pymongo/users.html
    """

    token = generate_key()
    result = messages.insert_one(        # call to insert msg below into database
        {
            "message_id": token,                 # Type: string
            "username": username,
            "msg_data": msg["msg_data"],    # Type: dict
            "msg_body": msg["msg_body"]               # Type: string
        })
    if result.acknowledged is not True:    # if add was unsuccessful
        return result, "Message Failed to Add"
    else:   # if add was successful
        return result, ""


def mongo_delMsg(message_id):
    """
    Reference: http://api.mongodb.com/python/current/api/pymongo/users.html
    """
    record = messages.find_one({"message_id": message_id})    # find msgs that have provided token
    result = messages.delete_one(record)     # delete found msg
    if result.deleted_count is 1:  # if successful
        return True, ""     # return True
    else:   # if unsuccessful
        return False, "Message Failed to Delete"


def mongo_delUser(user_id):
    """
    Reference: http://api.mongodb.com/python/current/api/pymongo/users.html
    """
    record = users.find_one({"user_id": user_id})    # find msgs that have provided token
    result = users.delete_one(record)     # delete found msg
    if result.deleted_count is 1:  # if successful
        return True, ""
    else:   # if unsuccessful
        return False, "User Failed to Delete"

def mongo_getMsgs(username):
    """
    placeholder for get all messages
    Reference: http://api.mongodb.com/python/current/api/pymongo/users.html
    """
    records = []
    for record in messages.find({"username": username}):  # call to find msgs with share the value in specific field
        del record['_id']
        records.append(record)  # add each msg to list
    # Sort the records by name:
    # records.sort(key=lambda i: i[field])    # sort list by value
    if len(records) == 0:
        return records
    else:
        return records


def mongo_getFriendMsgs(friends):
    """
    placeholder for get all messages
    Reference: http://api.mongodb.com/python/current/api/pymongo/users.html
    """
    records = []
    for friend in friends:
        for message in mongo_getMsgs(friend):
            records.append(message)
    # Sort the records by name:
    # records.sort(key=lambda i: i[field])    # sort list by value
    if len(records) == 0:
        return records, "No messages found"
    else:
        return records, ""

def mongo_getFieldList(field, value):
    """
    Reference: http://api.mongodb.com/python/current/api/pymongo/users.html
    """
    records = []
    for record in users.find({field: value}):  # call to find msgs with share the value in specific field
        del record['_id']
        records.append(record)  # add each msg to list
    # Sort the records by name:
    # records.sort(key=lambda i: i[field])    # sort list by

    if len(records) == 0:
        return records, "No messages found"
    else:
        return records, ""

def mongo_getUniqueValues(field):
    """
    Reference: http://api.mongodb.com/python/current/api/pymongo/users.html
    """
    all_types = users.distinct(field)  # call to get all unique values in specific field
    result = { field : all_types}
    if len(all_types) == 0:
        return result, "No Unique Fields Found"
    else:
        return result, ""

def mongo_clear():
    """
    placeholder to clear all messages in DB
    """
    userResult = users.delete_many({})
    msgResult = messages.delete_many({})
    if userResult.acknowledged and msgResult.acknowledged == True:
        return True, ""
    else:
        return False, "Failed to clear Database"
###
# Utility Functions
###

def generate_key():
    """
    Generates and returns a token of length 20
    """
    return binascii.hexlify(os.urandom(10)).decode()    # returns randomly generated token of length 20

###
# Temp tests
###

def mongo_tempTest():
    """
    temp tests:
    """

    result, error_msg = mongo_clear()
    assert result == True and error_msg == ""

    testResults = {
        "signup" : False,
        "login" : False,
        "addMsg": False,
        "getMsgs": False,
        "getFriendMsgs": False,
        "delMsg": False,
        "delUser": False,
        "clear": False
        }

    dummy1 = {
        "username": "test1",
        "password": "pwTest",
        "date_created": "2018-04-13T16:30:00.000Z",
    }

    dummy2 = {
        "username": "test2",
        "password": "pwTest2",
        "date_created": "2018-04-15T16:30:00.000Z",
    }

    message1 = {
        "msg_data": {},
        "msg_body": "Stuff here"
    }

    result, error_msg = mongo_signup(dummy1)
    assert result["username"] == dummy1["username"]
    assert error_msg == ""
    resDummy = result
    testResults["signup"] = True

    result, error_msg = mongo_login(dummy1)
    assert result["username"] == dummy1["username"]
    result, error_msg = mongo_login(dummy2)
    assert error_msg != ""
    testResults["login"] = True

    result, error_msg = mongo_addMsg(dummy1["username"], message1)
    print(result)
    assert error_msg == ""
    testResults["addMsg"] = True

    result = mongo_getMsgs(dummy1["username"])
    print(result)
    assert len(result) == 1
    testResults["getMsgs"] = True

    result, error_msg = mongo_addMsg(dummy2["username"], message1)
    friends = [dummy1["username"], dummy2["username"]]
    result, error_msg = mongo_getFriendMsgs(friends)
    assert len(result) == 2
    assert error_msg == ""
    testResults["getFriendsMsgs"] = True

    result, error_msg = mongo_delMsg(result[0]["message_id"])
    assert result == True
    assert error_msg == ""
    testResults["delMsg"] = True

    result, error_msg = mongo_delUser(resDummy["user_id"])
    assert result == True
    assert error_msg == ""
    testResults["delUser"] = True

    result, error_msg = mongo_clear()
    assert result == True
    assert error_msg == ""
    testResults["clear"] = True

    return testResults
