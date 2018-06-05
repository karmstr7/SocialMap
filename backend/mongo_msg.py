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

def mongo_login(act):
    """
    Reference: http://api.mongodb.com/python/current/api/pymongo/users.html
    """
    record = users.find_one({"username": act["username"]})
    result = {'username': '',
              'date_created': '',
              'friends': [],
              'user_id': '',
              'messages': [],
              'error_msg': ''}

    if record is None:
        result['error_msg'] = "user not found"
        return result, result['error_msg']

    if record['password'] != act['password']:
        result['error_msg'] = "password mismatch"
        return result, result['error_msg']

    result['username'] = record['username']
    result['date_created'] = record['date_created']
    result['friends'] = record['friends']
    result['user_id'] = record['user_id']
    # result['messages'] = mongo_getMsgs(record['user_id'])
    return result, "User Logged In"


def mongo_signup(act):
    """
    Reference: http://api.mongodb.com/python/current/api/pymongo/users.html
    """
    record = users.find_one({"username": act["username"]})
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
    new_act = {
        'username': act['username'],
        'password': act['password'],
        'date_created': act['date_created'],
        'friends': [],
        'user_id': user_id
    }
    users.insert_one(new_act)

    result['username'] = act['username']
    result['user_id'] = user_id
    result['date_created'] = act['date_created']
    return result, "User Signed Up"


def mongo_addMsg(username, msg):
    """
    Reference: http://api.mongodb.com/python/current/api/pymongo/users.html
    """

    token = generate_key()
    add_to_database = messages.insert_one(        # call to insert msg below into database
        {
            "message_id": token,                 # Type: string
            "username": username,
            "msg_data": msg["msg_data"],    # Type: dict
            "msg_body": msg["msg_body"]               # Type: string
        })

    result = {
        'message_id': token,
        'msg_body': msg["msg_body"],
        'error_msg': ""
    }

    if add_to_database.acknowledged is not True:    # if add was unsuccessful
        result['error_msg'] = "Message Failed to Add"
        return result, result['error_msg']
    else:   # if add was successful
        return result, result['error_msg']


def mongo_delMsg(token):
    """
    Reference: http://api.mongodb.com/python/current/api/pymongo/users.html
    """
    record = messages.find_one({"token": token})    # find msgs that have provided token
    ret = messages.delete_one(record)     # delete found msg
    if ret.deleted_count is 1:  # if successful
        return True     # return True
    else:   # if unsuccessful
        return False    # return False

def mongo_addFriend(username, friend):

    record = users.find_one({"username": friend})
    if record is None:
        return False, "friend not found"
    user = users.find_one({"username": username})
    user["friends"].append(friend)
    result = users.find_one_and_replace({"username": username}, user)

    if result != user:
        return True, ""
    else:
        return False, "Failed to Add Friend"


def mongo_delFriend(username, friend):

    record = users.find_one({"username": friend})
    if record is None:
        return False, "friend not found"
    user = users.find_one({"username": username})
    user["friends"].remove(friend)
    result = users.find_one_and_replace({"username": username}, user)

    if result != user:
        return True, ""
    else:
        return False, "Failed to Add Friend"

def mongo_getFriends(username):
    result ={
    'friends' : [],
    'error_msg' : ""
    }
    try:
        user = users.find_one({'username': username})
        result['friends'] = user['friends']
        print('FRIENDS RECEIVED FROM MONGODB:  ',result)
        return result
    except :
        result['error_msg'] = 'GET FRIENDS error'
        return result


    
def mongo_delUser(token):
    """
    Reference: http://api.mongodb.com/python/current/api/pymongo/users.html
    """
    record = users.find_one({"token": token})    # find msgs that have provided token
    ret = users.delete_one(record)     # delete found msg
    if ret.deleted_count is 1:  # if successful
        return True     # return True
    else:   # if unsuccessful
        return False    # return False


def mongo_getMsgs(username, friends):
    """
    placeholder for get all messages
    Reference: http://api.mongodb.com/python/current/api/pymongo/users.html
    """
    records = []
    for record in messages.find({"username": username}):  # call to find msgs with share the value in specific field
        del record['_id']
        records.append(record)  # add each msg to list

    for friend in friends:
        for message in messages.find({"username": friend}):
            del message['_id']
            records.append(message)
    # Sort the records by name:
    # records.sort(key=lambda i: i[field])    # sort list by value
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
    records.sort(key=lambda i: i[field])    # sort list by value
    return records  # return list

def mongo_getUniqueValues(field):
    """
    Reference: http://api.mongodb.com/python/current/api/pymongo/users.html
    """
    all_types = users.distinct(field)  # call to get all unique values in specific field
    result = { field : all_types}
    return result   # return all unique values found

def mongo_clear():
    """
    placeholder to clear all messages in DB
    """
    userResult = users.delete_many({})
    msgResult = messages.delete_many({})
    if userResult.acknowledged and msgResult.acknowledged == True:
        return True
    else:
        return False
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

    assert mongo_clear() == True

    testResults = {
        "signup" : False,
        "login" : False,
        "addMsg": False,
        "getMsgs": False,
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

    result = mongo_signup(dummy1)
    assert result["username"] == dummy1["username"]
    # assert len(mongo_getFieldList("username", "test1")) == 1
    resDummy = result
    testResults["signup"] = True

    result = mongo_login(dummy1)
    assert result["username"] == dummy1["username"]
    result = mongo_login(dummy2)
    assert result == 412
    testResults["login"] = True

    result = mongo_addMsg(dummy1["username"], message1)
    assert result != 412
    testResults["addMsg"] = True

    result = mongo_getMsgs(dummy1["username"])
    assert len(result) == 1
    testResults["getMsgs"] = True

    result = mongo_delMsg(result[0]["token"])
    assert result == True
    testResults["delMsg"] = True

    result = mongo_delUser(resDummy["token"])
    assert result == True
    testResults["delUser"] = True

    result = mongo_clear()
    assert result == True
    testResults["clear"] = True

    return testResults
