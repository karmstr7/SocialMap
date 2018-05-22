

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
    if record is not None and record["password"] == act["password"] :
        del record["_id"]
        record["messages"] = mongo_getMsgs(record["token"])
        return record    # return token of Correct Account
    else:
        return 412      # return error code

def mongo_signup(act):
    """
    Reference: http://api.mongodb.com/python/current/api/pymongo/users.html
    """
    record = users.find_one({"username": act["username"]})
    if record == None:
        token = generate_key()      # generate unique token for new msg

        newAct=     {
                        "username": act["username"],    # Type: string
                        "password": act["password"],    # Type: string
                        "date_created": act["date_created"],    # Type: string
                        "friends": [],    # Type: list of strings
                        "token": token    # Type: string
                    }

        result = users.insert_one(newAct)        # call to insert act below into database

        if result.acknowledged is True:    # if add was successful
            return newAct

    return 412 #  return error code for username already exists

def mongo_addMsg(username, msg):
    """
    Reference: http://api.mongodb.com/python/current/api/pymongo/users.html
    """

    token = generate_key()
    res = messages.insert_one(        # call to insert msg below into database
        {
            "token": token,                 # Type: string
            "username": username,
            "msg_data": msg["msg_data"],    # Type: dict
            "msg_body": msg["msg_body"]               # Type: string
        })
    if res.acknowledged is not True:    # if add was unsuccessful
        return 412      # return error code
    else:   # if add was successful
        return token    # return token of added msg


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
    return records  # return list

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
