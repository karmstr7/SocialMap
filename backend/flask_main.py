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


@app.route('/socialmap/api/login', methods=['GET'])
def login():
    app.logger.debug("ATTEMPT TO LOG IN USER")
    app.logger.debug(request.json)

    username = request.json['username']
    password = request.json['password']

    acc = {
        'username': username,
        'password': password
    }

    # Verify user here
    result, error_msg = mongo_login(acc)
    app.logger.debug("ERROR DURING LOGIN: {}".format(error_msg))
    return jsonify(result), 200


@app.route('/socialmap/api/signup', methods=['POST'])
def signup():
    app.logger.debug("ATTEMPT TO CREATE USER")
    app.logger.debug(request.json)

    username = request.json['username']
    password = request.json['password']
    date = request.json['date_created']

    acc = {
        'username': username,
        'password': password,
        'date_created': date
    }

    result, error_msg = mongo_signup(acc)
    app.logger.debug("ERROR DURING SIGNUP: {}".format(error_msg))
    return jsonify(result), 200


@app.route('/socialmap/api/addMsg', methods=['POST'])
def addMsg():
    app.logger.debug("ATTEMPT TO ADD MSG")
    app.logger.debug(request.json)

    username = request.json['username']
    msg_data = request.json['msg_data']
    msg_body = request.json['msg_body']

    msg = {
        "username": username,
        "msg_data": msg_data,
        "msg_body": msg_body
    }

    result, error_msg = mongo_addMsg(msg)
    # On success, return the user's ID
    app.logger.debug("ERROR DURING ADDMSG: {}".format(error_msg))
    return jsonify({"error_msg": error_msg}), 200


@app.route('/socialmap/api/addFriend', methods=['POST'])
def addFriend():
    app.logger.debug("ATTEMPT TO ADD MSG")
    app.logger.debug(request.json)

    username = request.json['username']
    friend = request.json['friend']

    result, error_msg = mongo_addFriend(username, friend)
    # On success, return the user's ID
    app.logger.debug("ERROR DURING ADDFRIEND: {}".format(error_msg))
    return jsonify({"error_msg": error_msg}), 200


@app.route('/socialmap/api/delFriend', methods=['POST'])
def delFriend():
    app.logger.debug("ATTEMPT TO ADD MSG")
    app.logger.debug(request.json)

    username = request.json['username']
    friend = request.json['friend']

    result, error_msg = mongo_delFriend(username, friend)
    # On success, return the user's ID
    app.logger.debug("ERROR DURING DELFRIEND: {}".format(error_msg))
    return jsonify({"error_msg": error_msg}), 200


@app.route('/socialmap/api/delMsg', methods=['POST'])
def delMsg():
    app.logger.debug("ATTEMPT TO DELETE MSG")
    app.logger.debug(request.json)

    message_id = request.json['message_id']

    result, error_msg = mongo_delMsg(token)
    # On success, return the user's ID
    app.logger.debug("ERROR DURING DELMSG: {}".format(error_msg))
    return jsonify({"error_msg": error_msg}), 200


@app.route('/socialmap/api/delUser', methods=['POST'])
def delUser():
    app.logger.debug("ATTEMPT TO DELETE USER")
    app.logger.debug(request.json)

    user_id = request.json['user_id']

    result, error_msg = mongo_delUser(user_id)
    # On success, return the user's ID
    app.logger.debug("ERROR DURING DELUSER: {}".format(error_msg))
    return jsonify({"error_msg": error_msg}), 200


@app.route('/socialmap/api/getMsgs', methods=['GET'])
def getMsgs():
    app.logger.debug("ATTEMPT TO USER MESSAGES")
    app.logger.debug(request.json)

    username = request.json['username']

    result = mongo_getMsgs(username)
    if len(result) == 0:
        error_msg = "No Messages Found"
    else:
        error_msg = ""
    # On success, return the user's ID
    app.logger.debug("ERROR DURING GETMSGS: {}".format(error_msg))
    return jsonify({"result": result, "error_msg": error_msg}), 200


@app.route('/socialmap/api/getFriendMsgs', methods=['GET'])
def getFriendMsgs():
    app.logger.debug("ATTEMPT TO FRIEND MESSAGES")
    app.logger.debug(request.json)

    friends = request.json['friends']

    result, error_msg = mongo_getFriendMsgs(friends)
    # On success, return the user's ID
    app.logger.debug("ERROR DURING GETFRIENDMSGS: {}".format(error_msg))
    return jsonify({"result": result, "error_msg": error_msg}), 200


@app.route('/socialmap/api/clear', methods=['POST'])
def clear():
    app.logger.debug("ATTEMPT TO CLEAR DATABASE")
    app.logger.debug(request.json)

    result, error_msg = mongo_clear()
    # On success, return the user's ID
    app.logger.debug("ERROR DURING CLEAR: {}".format(error_msg))
    return jsonify({"error_msg": error_msg}), 200


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
    app.logger.setLevel(logging.DEBUG)
    # app.logger.debug(mongo_tempTest())
    app.run(port=CONFIG.PORT, host="localhost")
