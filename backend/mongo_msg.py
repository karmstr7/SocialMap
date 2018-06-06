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
# Helper Functions
###

def mongo_login(act):
    """
    Login a user and return the user's account object
    Arguements:
        Object: account (String: username String: password)
    Returns:
        Object: account String: error_msg
    Reference: http://api.mongodb.com/python/current/api/pymongo/
    """
    result = {'username': '',                   # Type: string
              'date_created': '',               # Type: string
              'friends': [],                    # Type: list of strings
              'user_id': '',                    # Type: string
              'error_msg': ''}                  # Type: string

    # find user account in database
    record = users.find_one({"username": act["username"]})

    if record is None:      # if user doesn't exist in the database
        result['error_msg'] = "user not found"      # return error
        return result

    if record['password'] != act['password']:       # if passwords don't match
        result['error_msg'] = "password mismatch"   # return error
        return result

    # set result to be user's account object information:
    result['username'] = record['username']             # Type: string
    result['date_created'] = record['date_created']     # Type: string
    result['friends'] = record['friends']               # Type: list of strings
    result['user_id'] = record['user_id']               # Type: string
    return result


def mongo_signup(act):
    """
    Adds an account object with a unique ID to the database
    Arguements:
        Object: account
    Returns:
        String: error_msg
    Reference: http://api.mongodb.com/python/current/api/pymongo/
    """
    result = {'username': '',           # Type: string
              'user_id': '',            # Type: string
              'date_created': '',       # Type: string
              'friends': [],            # Type: list of strings
              'error_msg': ''}          # Type: string

    # find account in the database, to check if it exists already
    record = users.find_one({"username": act["username"]})

    if record is not None:      # if user already exists
        result['error_msg'] = "user already exists"     # return error
        return result

    user_id = generate_key()        # generate unique id of type string
    new_act = {
        'username': act['username'],            # Type: string
        'password': act['password'],            # Type: string
        'date_created': act['date_created'],    # Type: string
        'friends': [],                          # Type: list of strings
        'user_id': user_id                      # Type: string
    }
    users.insert_one(new_act)

    result['username'] = act['username']            # Type: string
    result['user_id'] = user_id                     # Type: string
    result['date_created'] = act['date_created']    # Type: string
    return result


def mongo_addMsg(username, msg):
    """
    Adds a message with a unique ID to the database
    Arguements:
        String: username, Object: message
    Returns:
        String: error_msg
    Reference: http://api.mongodb.com/python/current/api/pymongo/
    """

    token = generate_key()
    result = {
        'message_id': token,                # Type: string
        'msg_body': msg["msg_body"],        # Type: string
        'msg_data': msg["msg_data"],        # Type: dict
        'error_msg': ""                     # Type: string
    }

    add_to_database = messages.insert_one(          # call to insert msg below into database
        {
            "message_id": token,                    # Type: string
            "username": username,                   # Type: string
            "msg_data": msg["msg_data"],            # Type: dict
            "msg_body": msg["msg_body"]             # Type: string
        })

    if add_to_database.acknowledged is not True:    # if add was unsuccessful
        result['error_msg'] = "Message Failed to Add"   # return error
        return result
    else:                                           # if add was successful
        return result


def mongo_delMsg(message_id):
    """
    Deletes a specific message based on message_id from the database
    Arguements:
        String: message_id
    Returns:
        String: error_msg
    Reference: http://api.mongodb.com/python/current/api/pymongo/
    """
    result = {
        'error_msg': ''             # Type: string
    }
        # find specifc message that have provided id
    record = messages.find_one({'message_id': message_id})

    ret = messages.delete_one(record)               # delete found msg

    if ret.deleted_count is 1:                      # if successful
        return result
    else:                                           # if unsuccessful
        result['error_msg'] = "could not delete message"    # return error
        return result


def mongo_addFriend(username, friend):
    """
    Adds a friend's username to a user's friend list
    Arguements:
        String: username, String: friend (username)
    Returns:
        String: error_msg
    Reference: http://api.mongodb.com/python/current/api/pymongo/
    """
    result = {
        'error_msg': ''                 # Type: string
    }

    record = users.find_one({"username": friend})  # find friend account object

    if record is None:                             # if user doesn't exist
        result['error_msg'] = "friend not found"    # return error
        return result

    user = users.find_one({'username': username})   # find user account object
    user['friends'].append(friend)                  # add friend to user's list
    # update user's account on the database
    updated_user = users.find_one_and_replace({'username': username}, user)
    if updated_user != user:    # if user was updated
        return result
    else:                       # if user wasn't updated
        result['error_msg'] = "Failed to add friend"    # return error
        return result


def mongo_delFriend(username, friend):
    """
    Removes a friend's username from a user's friend list
    Arguements:
        String: username, String: friend (username)
    Returns:
        String: error_msg
    Reference: http://api.mongodb.com/python/current/api/pymongo/
    """
    result = {
        'error_msg': ''                 # Type: string
    }

    record = users.find_one({"username": friend})   # find friend's account

    if record is None:                              # if friend doesn't exist
        result['error_msg'] = "friend not found"    # return error
        return result

    user = users.find_one({"username": username})   # find user's account
    if friend in user["friends"]:
        user["friends"].remove(friend)                  # remove friend from list
    # update user's account object in the database
    updated_user = users.find_one_and_replace({"username": username}, user)

    if updated_user != user:    # if user was updated
        return result
    else:                       # if user wasn't updated
        result['error_msg'] = "Failed to Delete Friend" # return error
        return result


def mongo_getFriends(username):
    """
    Gets a list of friends from a user in the database
    Arguements:
        String: username
    Returns:
        List of Strings: friends (usernames), String: error_msg
    Reference: http://api.mongodb.com/python/current/api/pymongo/
    """
    result = {
        'friends': [],                  # Type: list of strings
        'error_msg': ''                 # Type: string
    }

    try:
        user = users.find_one({'username': username})   # find user in database
        result['friends'] = user['friends']     # grab list of friends
        return result
    except:
        result['error_msg'] = 'Could not get friends'   # if failed
        return result


def mongo_delUser(username):
    """
    Deletes a user from the database based on username
    Arguements:
        String: username
    Returns:
        String: error_msg
    Reference: http://api.mongodb.com/python/current/api/pymongo/
    """
    result = {
        'error_msg': ''                 # Type: string
    }

    user = users.find_one({"username": username})                   # find user
    if user is None:
        result["error_msg"] = "user doesn't exist"
        return result
    response = users.delete_one(user)                               # delete user

    # if successful
    if response.deleted_count is 1:
        # find messages associated with this user
        for message in messages.find({'username': username}):
            messages.delete_one(message)                             # delete the message

        for account in users.find({}):
            mongo_delFriend(account["username"], username)

        # Need to delete user from friendlists of other users
        return result
    else:                                                       # if unsuccessful
        result['error_msg'] = "Could not delete account"
        return result


def mongo_getMsgs(username, friends):
    """
    Grabs messages from the database and returns them
    Arguements:
        String: username, List of Strings: friends (usernames)
    Returns:
        List of Objects: messages, String: error_msg
    Reference: http://api.mongodb.com/python/current/api/pymongo/
    """
    result = {
        'result': [],                  # Type: list of message objects
        'error_msg': ''                 # Type: string
    }

    records = []
    # call to find msgs with share the value in specific field:
    for record in messages.find({"username": username}):
        del record['_id']
        records.append(record)  # add each msg to list
    # call to grab friend messages
    for friend in friends:
        for message in messages.find({"username": friend}):
            del message['_id']
            records.append(message)

    result['result'] = records            # set message found to result

    return result


def mongo_checkUsername(username):
    """
    Checks if username currently exists in the database
    Arguements:
        String: username
    Returns:
        String: error_msg
    Reference: http://api.mongodb.com/python/current/api/pymongo/
    """
    result = {
        'error_msg': ''                 # Type: string
    }

    records = []
    for record in users.find({"username": username}):  # call to find msgs with share the value in specific field
        records.append(record)

    if len(records) == 0:
        result['error_msg'] = "No username found"
        return result
    else:
        return result


def mongo_clear():
    """
    Clears entire database of users and messages
    Arguements:
        None
    Returns:
        Boolean
    Reference: http://api.mongodb.com/python/current/api/pymongo/
    """
    userResult = users.delete_many({})          # clear user collection
    msgResult = messages.delete_many({})        # clear message collection
    if userResult.acknowledged and msgResult.acknowledged == True:  # if complete
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
    # returns randomly generated token of length 20
    return binascii.hexlify(os.urandom(10)).decode()

###
# Temp tests
###

def mongo_tempTest():
    """
    temp tests:
    """

    result = mongo_clear()
    assert result == True

    testResults = {
        "signup" : False,
        "login" : False,
        "addFriend" : False,
        "delFriend" : False,
        "getFriends": False,
        "addMsg": False,
        "delMsg": False,
        "getMsgs": False,
        "delUser": False,
        "checkUsername": False,
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
    assert result["error_msg"] == ""
    testResults["signup"] = True

    result = mongo_login(dummy1)
    resDummy = result
    assert result["username"] == dummy1["username"]
    assert result["error_msg"] == ""
    result = mongo_login(dummy2)
    assert result["error_msg"] != ""
    testResults["login"] = True

    result = mongo_checkUsername(dummy1["username"])
    assert result["error_msg"] == ""
    result = mongo_checkUsername(dummy2["username"])
    assert result["error_msg"] != ""
    testResults["checkUsername"] = True

    result = mongo_addFriend(dummy1["username"], dummy2["username"])
    assert result["error_msg"] != ""
    result = mongo_signup(dummy2)
    result = mongo_addFriend(dummy1["username"], dummy2["username"])
    assert result["error_msg"] == ""
    testResults["addFriend"] = True

    result = mongo_delFriend(dummy1["username"], dummy2["username"])
    assert result["error_msg"] == ""
    result = mongo_delFriend(dummy1["username"], dummy2["username"])
    assert result["error_msg"] != ""
    testResults["delFriend"] = True

    result = mongo_addMsg(dummy1["username"], message1)
    assert result["error_msg"] == ""
    testResults["addMsg"] = True

    result = mongo_getMsgs(dummy1["username"], [])
    assert len(result["result"]) == 1
    assert result["error_msg"] == ""
    result = mongo_addMsg(dummy2["username"], message1)
    result = mongo_getMsgs("", [dummy1["username"], dummy2["username"]])
    assert len(result["result"]) == 2
    assert result["error_msg"] == ""
    testResults["getMsgs"] = True

    result = mongo_delMsg(result["result"][0]["message_id"])
    assert result["error_msg"] == ""
    testResults["delMsg"] = True

    result = mongo_delUser(resDummy["username"])
    assert result["error_msg"] == ""
    testResults["delUser"] = True

    result = mongo_clear()
    assert result == True
    testResults["clear"] = True

    return testResults
