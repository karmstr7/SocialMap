

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
    collection = db.resources
except:
    print("Failure opening database. Is Mongo running? Correct password?")
    sys.exit(1)


###
# Mongo msg Functions
###

def mongo_addMsg(msg):
    """
    Reference: http://api.mongodb.com/python/current/api/pymongo/collection.html
    """

    token = generate_key()      # generate unique token for new msg
    res = collection.insert_one(        # call to insert msg below into database
        {
            "placeholder": msg["placeholder"],    # Type: string
        })
    if res.acknowledged is not True:    # if add was unsuccessful
        return 412      # return error code
    else:   # if add was successful
        return token    # return token of added msg

def mongo_editMsg(msg):
    """
    Reference: http://api.mongodb.com/python/current/api/pymongo/collection.html
    """

    res = collection.find_one_and_replace(      # call to find and replace msg in database
        {"token": msg["token"]},      # uses token of provided msg to find it
        {
            "placeholder": msg["placeholder"],    # Type: string
        }
        )

    if res["token"] == msg["token"]:  # if returned msg has same token
        return True     # return True
    else:   # if unsuccessful
        return 413      # return error code


def mongo_delMsg(token):
    """
    Reference: http://api.mongodb.com/python/current/api/pymongo/collection.html
    """
    for record in collection.find({"token": token}):    # find msgs that have provided token
        ret = collection.delete_one(record)     # delete found msg
        if ret.deleted_count is 1:  # if successful
            return True     # return True
        else:   # if unsuccessful
            return False    # return False

def mongo_getUser(token):
    """
    placeholder for get user
    Reference: http://api.mongodb.com/python/current/api/pymongo/collection.html
    """
    record = collection.find_one({"token": token})  # find msg that has provided token
    if record == None:  # if there are no msgs
        return 415  # return error code
    else:   # if msg found
        del record['_id']
        return record   # return msg

def mongo_getMsgs():
    """
    placeholder for get all messages
    Reference: http://api.mongodb.com/python/current/api/pymongo/collection.html
    """
    records = []
    for record in collection.find():    # get all msgs in database
        del record['_id']
        records.append(record)  # add each msg to list
    return records  # return list of all msgs in database

def mongo_getFieldList(field, value):
    """
    Reference: http://api.mongodb.com/python/current/api/pymongo/collection.html
    """
    records = []
    for record in collection.find({field: value}):  # call to find msgs with share the value in specific field
        del record['_id']
        records.append(record)  # add each msg to list
    # Sort the records by name:
    records.sort(key=lambda i: i[field])    # sort list by value
    return records  # return list

def mongo_clear():
    """
    placeholder to clear all messages in DB
    """
    tokens = mongo_getFieldList("token")    # call to get all unique tokens in database
    for token in tokens['token']:   # for each unique token
        mongo_delmsg(token)   # delete each msg with that token
    result = mongo_getFieldList("token")    # call again to get each unique token
    if len(result["token"]) is 0:   # if database is empty
        return True     # return True
    else:   # if database is not empty
        return False    # return False

###
# Utility Functions
###

def generate_key():
    """
    Generates and returns a token of length 20
    """
    return binascii.hexlify(os.urandom(10)).decode()    # returns randomly generated token of length 20
