from flask import jsonify, abort, make_response, request
import flask  # Web server tool.
from mongo_msg import *  # Mongo code
import config  # Get config settings from credentials file

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
    """
    Calls helper function to log user into service
    Arguements:
        String: username, String: password
    Returns:
        Object: account String: error_msg
    """
    app.logger.debug("ATTEMPT TO LOG IN USER")

    username = request.json['username']     # Type: String
    password = request.json['password']     # Type: String

    # create acount object
    act = {
        'username': username,
        'password': password
    }

    result = mongo_login(act)

    if result['error_msg'] is not "":
        app.logger.debug('ERROR DURING LOGIN: {}'.format(result['error_msg']))
    else:
        app.logger.debug('LOGGED IN USER: {}'.format(result['username']))

    return jsonify(result), 200


@app.route('/socialmap/api/signup', methods=['POST'])
def signup():
    """
    Calls helper function to sign user up for service
    Arguements:
        String: username, String: password, String: date_created
    Returns:
        Object: account, String: error_msg
    """
    app.logger.debug("ATTEMPT TO CREATE USER")

    username = request.json['username']     # Type: String
    password = request.json['password']     # Type: String
    date = request.json['date_created']     # Type: String

    # create acount object
    act = {
        'username': username,
        'password': password,
        'date_created': date
    }

    result = mongo_signup(act)      # call helper function

    if result['error_msg'] is not "":
        app.logger.debug('ERROR DURING SIGNUP: {}'.format(result['error_msg']))
    else:
        app.logger.debug('CREATED NEW ACCOUNT: {}'.format(result['username']))

    return jsonify(result), 200


@app.route('/socialmap/api/addMsg', methods=['POST'])
def addMsg():
    """
    Calls helper function to add message to database
    Arguements:
        String: username, Dict: msg_data, String: msg_body
    Returns:
        Object: message, String: error_msg
    """
    app.logger.debug("ATTEMPT TO ADD MSG")

    username = request.json['username']     # Type: String
    msg_data = request.json['msg_data']     # Type: Dict
    msg_body = request.json['msg_body']     # Type: String

    # create message object
    msg = {
        "msg_data": msg_data,
        "msg_body": msg_body
    }

    result = mongo_addMsg(username, msg)    # call to helper function

    if result['error_msg'] is not "":
        app.logger.debug('ERROR DURING ADD MESSAGE: {}'.format(result['error_msg']))
    else:
        app.logger.debug('ADDED NEW MESSAGE: {}'.format(result['msg_body']))

    return jsonify(result), 200


@app.route('/socialmap/api/delMsg', methods=['POST'])
def delMsg():
    """
    Calls helper function to delete message to database
    Arguements:
        String: message_id
    Returns:
        String: error_msg
    """
    app.logger.debug("ATTEMPT TO DELETE MSG")

    message_id = request.json['message_id']     # Type: String

    result = mongo_delMsg(message_id)   # call to helper function

    if result['error_msg'] is not "":
        app.logger.debug('ERROR DURING DELETE MESSAGE {}: {}'.format(message_id, result['error_msg']))
    else:
        app.logger.debug('DELETED MESSAGE {}'.format(message_id))

    return jsonify(result), 200


@app.route('/socialmap/api/addFriend', methods=['POST'])
def addFriend():
    """
    Calls helper function to add friend user's friend list
    Arguements:
        String: username, String: friend (username)
    Returns:
        String: error_msg
    """
    app.logger.debug("ATTEMPT TO ADD FRIEND")

    username = request.json['username']     # Type: String
    friend = request.json['friend']         # Type: String

    result = mongo_addFriend(username, friend)  # call to helper function

    if result['error_msg'] is not "":   # if there was an error, report it
        app.logger.debug('ERROR DURING ADDING {} TO {}: {}'.format(username, friend, result['error_msg']))
    else:
        app.logger.debug('ADDED {} AS FRIEND TO {}'.format(friend, username))

    return jsonify(result), 200


@app.route('/socialmap/api/delFriend', methods=['POST'])
def delFriend():
    """
    Calls helper function to remove friend from user's friend list
    Arguements:
        String: username, String: friend (username)
    Returns:
        String: error_msg
    """
    app.logger.debug("ATTEMPT TO DELETE FRIEND")

    username = request.json['username']     # Type: String
    friend = request.json['friend']         # Type: String

    result = mongo_delFriend(username, friend)  # call to helper function

    if result['error_msg'] is not "":
        app.logger.debug('ERROR DURING DELETING FRIEND {} FROM USER {}: {}'.format(friend, username, result['error_msg']))
    else:
        app.logger.debug('DELETED {} AS FRIEND FROM {}'.format(friend, username))

    return jsonify(result), 200


@app.route('/socialmap/api/getFriends', methods = ['POST'])
def getFriends():
    """
    Calls helper function to get friend list of a user
    Arguements:
        String: username
    Returns:
        List of Strings: friends (usernames), String: error_msg
    """
    app.logger.debug("ATTEMPT TO GET FRIENDS")

    username = request.json['username']     # Type: String

    result = mongo_getFriends(username) # call to helper function

    if result['error_msg'] is not "":
        app.logger.debug('ERROR DURING GETTING FRIENDS FOR {}: {}'.format(username, result['error_msg']))
    else:
        app.logger.debug('GOT FRIEND LIST FOR {}'.format(username))

    return jsonify(result), 200


@app.route('/socialmap/api/delUser', methods=['POST'])
def delUser():
    """
    Calls helper function to delete an account from the database
    Arguements:
        String: username
    Returns:
        String: error_msg
    """
    app.logger.debug("ATTEMPT TO DELETE USER")

    username = request.json['username']     # Type: String

    result = mongo_delUser(username)    # call to helper function

    if result['error_msg'] is not "":
        app.logger.debug('ERROR DURING DELETING USER {}: {}'.format(username, result['error_msg']))
    else:
        app.logger.debug('DELETED USER {}'.format(username))

    return jsonify(result), 200


@app.route('/socialmap/api/getMsgs', methods=['POST'])
def getMsgs():
    """
    Calls helper function to get messages of user and their friends
    Arguements:
        String: username, List of Strings: friends (usernames)
    Returns:
        List of Objects: messages, String: error_msg
    """
    app.logger.debug("ATTEMPT TO GET MESSAGES")

    username = request.json['username']     # Type: String
    friends = request.json['friends']       # Type: String

    result = mongo_getMsgs(username, friends)   # call to helper function

    app.logger.debug("Messages: {}".format(result['result']))

    return jsonify(result), 200


@app.route('/socialmap/api/clear', methods=['POST'])
def clear():
    """
    Calls helper function to add message to database
    Arguements:
        None
    Returns:
        Boolean
    """
    app.logger.debug("ATTEMPT TO CLEAR DATABASE")

    result = mongo_clear()  # call to helper function

    if result is True:
        app.logger.debug("DATABASE CLEARED")
        return jsonify(result), 201

    else:
        app.logger.debug("FAILED TO CLEAR DATABASE")
        return jsonify(result)


# Error handlers
@app.errorhandler(400)
def format_error(error):
    return make_response(jsonify({'error': 'Invalid format'}), 400)


@app.errorhandler(403)
def forbidden_error(error):
    return make_response(jsonify({'error': 'Forbidden'}), 403)


@app.errorhandler(404)
def not_found_error(error):
    return make_response(jsonify({'error': 'Not Found'}), 404)


@app.errorhandler(500)
def server_error(error):
    return make_response(jsonify({'error': 'Server error'}), 500)


if __name__ == "__main__":
    app.debug = CONFIG.DEBUG
    # app.logger.setLevel(logging.DEBUG)
    # app.logger.debug(mongo_tempTest())
    app.run(port=CONFIG.PORT, host="localhost")
    # app.run(port=CONFIG.PORT, host="149.28.12.19")
