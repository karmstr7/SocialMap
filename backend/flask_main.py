from flask import jsonify, abort, make_response, request
import flask  # Web server tool.
from mongo_msg import *  # Mongo code
import config  # Get config settings from credentials file
import logging

####
# App globals:
###
CONFIG = config.configuration()

app = flask.Flask(__name__)
app.secret_key = CONFIG.SECRET_KEY


###
# REST API SERVICE
###


@app.route('/socialmap/api/login', methods=['POST'])
def login():
    print("ATTEMPT TO LOG IN USER")
    print(request.json)
    username = request.json['username']
    password = request.json['password']
    # Verify user here
    # Get the friends list
    friends = []
    # Get the messages
    messages = []

    # On success, return the user's ID
    return jsonify({'id': 'something unique'}), 201


@app.route('/socialmap/api/signup', methods=['POST'])
def signup():
    print("ATTEMPT TO CREATE USER")
    print(request.json)
    username = request.json['username']
    password = request.json['password']
    # Check for pre-existing account of the same name
    # Create ID for user, empty friends list, and empty message list
    # Will include date_of_creation later on

    # On success, return the user's ID
    return jsonify({'id': 'something unique', 'friends': [], 'messages': []}), 201


###
# URL AJAX Routing
###


@app.route("/_addmsg")
def addMsg():

    app.logger.debug("_addmsg")
    # Get msg information

    msg = {
        "placeholder": flask.request.args.get("placeholder", type=str),  # Type: string
    }

    ret = mongo_addMsg(msg)  # Call to helper function, passes msg above

    if ret != 412 or ret != 416:  # check for error codes
        app.logger.debug("Inserted")
        result = {"message": "msg Inserted!",  # message for success
                  "status": 201,  # status code for success
                  "token": ret  # token of added msg
                  }
        return flask.jsonify(result=result)
    else:  # return error codes with message
        app.logger.debug(ret)
        result = {"error": "Failed to Add msg",  # error message
                  "status": ret  # status code returned from helper
                  }
        return flask.jsonify(result=result)


@app.route("/_editMsg")
def editmsg():

    app.logger.debug("_editMsg")
    msg = {
        "placeholder": flask.request.args.get("placeholder", type=str),  # Type: string
        "token": flask.request.args.get("token", type=str)  # unique token for msg, length 20
    }

    ret = mongo_editMsg(msg)  # Call to helper function, passes msg above

    if ret is True:  # check to see if it was successful
        app.logger.debug("msg edited")
        result = {"message": "msg Edited!",  # message for success
                  "status": 202,  # status code
                  "token": ret  # token of edited msg
                  }
        return flask.jsonify(result=result)  # return result to front end

    else:  # if it wasn't successful
        app.logger.debug("Failed to edit msg")
        result = {"error": "Failed to edit msg",  # error message
                  "status": ret  # status code returned from helper
                  }
        return flask.jsonify(result=result)  # return result to front end


@app.route("/_delMsg")
def delmsg():

    app.logger.debug("_delMsg")
    # Get token
    token = flask.request.args.get("token", type=str)  # database token of length 20

    ret = mongo_delMsg(token)  # Call to helper function, passes token from above

    if ret is True:  # if successful
        app.logger.debug("Deleted")
        result = {"message": "msg Deleted!",  # message for success
                  "status": 203  # status code for success
                  }
        return flask.jsonify(result=result)

    else:  # if unsuccessful
        app.logger.debug(ret)
        result = {"error": "Failed to Delete msg",  # error message
                  "status": 414  # status code
                  }
        return flask.jsonify(result=result)


@app.route("/_getUser")
def getUser():

    app.logger.debug("_getmsg")
    # Get token
    token = flask.request.args.get("token", type=str)  # database token of length 20

    ret = mongo_getUser(token)  # Call to helper function, passes token from above

    if ret != 415:  # if successful
        app.logger.debug("Retrieved User")
        result = {
            "message": "User Retrieved!",  # message for success
            "msg": ret,  # msg returned from helper
            "status": 204  # status code for success
        }
        return flask.jsonify(result=result)  # return result to front end

    else:  # if unsuccessful
        app.logger.debug("Failed to get User")
        result = {"error": "Failed to get User",  # error message
                  "status": ret  # status code from helper
                  }
        return flask.jsonify(result=result)  # return result to front end

@app.route("/_getMsgs")
def getMsgs():

    app.logger.debug("_getAllmsgs")
    # No arguments

    msgs = mongo_getMsgs()  # Call to helper to retrieve all msgs, passes nothing

    if len(msgs) > 0:  # if list contains msgs
        app.logger.debug("Got msgs")
        result = {
            "message": "msgs Retrieved!",  # message for success
            "num": len(msgs),  # number of msgs
            "msgs": msgs,  # list of msgs
            "status": 204  # status code for success
        }
        return flask.jsonify(result=result)  # return result to front end

    else:
        app.logger.debug("Retrieved No msgs")
        result = {"error": "Retrieved No msgs",  # error message
                  "status": 418  # error code
                  }
        return flask.jsonify(result=result)  # return result to front end


@app.route("/_getFieldList")
def getFieldList():

    app.logger.debug("_getFieldList")
    # Get request field
    field = flask.request.args.get("field", type=str)  # name of field

    fieldList = mongo_getFieldList(field)  # Call helper to get list of values in field, passes field name

    if len(fieldList) > 0:  # if list has values
        app.logger.debug("Got List")
        result = {
            "message": "List Retrieved!",  # message for success
            "num": len(fieldList),  # number of unique values
            "list": fieldList,  # list of unique values
            "status": 205  # status code for success
        }
        return flask.jsonify(result=result)  # return result to front end

    else:  # if list was empty
        app.logger.debug("Empty field list Returned")
        result = {"error": "Empty Field List Returned",  # error message
                  "status": 419  # error code
                  }
        return flask.jsonify(result=result)  # return result to front end


@app.route("/_clear")
def clear():
    """
    Deletes all msgs in database:
    Calls: helper function to delete all msgs.
    Returns: status code based on if it was successful or not.
    """
    result = mongo_clear()  # Call helper function to clear database

    if result == True:
        app.logger.debug("All msgs deleted")
        result = {"message": "All msgs Deleted",  # message for success
                  "status": 206  # status code for success
                  }
        return flask.jsonify(result=result)  # return result to front end

    else:
        app.logger.debug("Not all msgs were deleted")
        result = {"error": "Database is not Empty",  # error message
                  "status": 420  # error code
                  }
        return flask.jsonify(result=result)  # return result to front end


# Error page(s)
@app.errorhandler(404)
def not_found(error):
    return make_response(jsonify({'error': 'Not Found'}), 404)


@app.errorhandler(400)
def format_error(error):
    return make_response(jsonify({'error': 'Invalid format'}),400)


@app.errorhandler(500)
def server_error(error):
    return make_response(jsonify({'error': 'Server error'}), 500)


if __name__ == "__main__":
    app.debug = CONFIG.DEBUG
    app.logger.setLevel(logging.DEBUG)
    app.run(port=CONFIG.PORT, host="localhost")
