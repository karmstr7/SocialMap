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
    app.logger.debug("ATTEMPT TO LOG IN USER")
    app.logger.debug(request.json)

    username = request.json['username']
    password = request.json['password']

    act = {
        'username': username,
        'password': password
    }

    # Verify user here
    result, error_msg = mongo_login(act)
    print(result)
    app.logger.debug("ERROR DURING LOGIN: {}".format(error_msg))
    return jsonify(result), 200


@app.route('/socialmap/api/signup', methods=['POST'])
def signup():
    app.logger.debug("ATTEMPT TO CREATE USER")
    app.logger.debug(request.json)

    username = request.json['username']
    password = request.json['password']
    # date = request.json['date_created']

    act = {
        'username': username,
        'password': password,
        'date_created': ''
    }

    result, error_msg = mongo_signup(act)
    print(result)
    app.logger.debug("ERROR DURING SIGNUP: {}".format(error_msg))
    return jsonify(result), 200

    # # On success, return the user's ID
    # if result != 412:
    #     print('result {}'.format(result))
    #     result["messages"] = []
    #     app.logger.debug("USER CREATED")
    #     return jsonify(result), 201
    #
    # else:
    #     app.logger.debug("USER FAILED TO CREATE")
    #     return jsonify(result)


@app.route('/socialmap/api/addMsg', methods=['POST'])
def addMsg():
    app.logger.debug("ATTEMPT TO ADD MSG")
    app.logger.debug(request.json)

    username = request.json['username']
    msg_data = request.json['msg_data']
    msg_body = request.json['msg_body']
    # date = request.json['date_created']

    msg = {
        "username": username,
        "msg_data": msg_data,
        "msg_body": msg_body
    }

    result = mongo_addMsg(msg)
    # On success, return the user's ID
    if result != 412:
        app.logger.debug("MSG ADDED")
        return jsonify(result), 201

    else:
        app.logger.debug("MSG FAILED TO ADD")
        return jsonify(result)


@app.route('/socialmap/api/delMsg', methods=['POST'])
def delMsg():
    app.logger.debug("ATTEMPT TO DELETE MSG")
    app.logger.debug(request.json)

    token = request.json['token']

    result = mongo_delMsg(token)
    # On success, return the user's ID
    if result != 412:
        app.logger.debug("MSG DELETED")
        return jsonify(result), 201

    else:
        app.logger.debug("MSG FAILED TO DELETE")
        return jsonify(result)


@app.route('/socialmap/api/delUser', methods=['POST'])
def delUser():
    app.logger.debug("ATTEMPT TO DELETE USER")
    app.logger.debug(request.json)

    token = request.json['token']

    result = mongo_delUser(token)
    # On success, return the user's ID
    if result == True:
        app.logger.debug("USER DELETED")
        return jsonify(result), 201

    else:
        app.logger.debug("USER FAILED TO DELETE")
        return jsonify(result)


@app.route('/socialmap/api/getMsgs', methods=['POST'])
def getMsgs():
    app.logger.debug("ATTEMPT TO USER MESSAGES")
    app.logger.debug(request.json)

    username = request.json['username']

    result = mongo_getMsgs(username)
    # On success, return the user's ID
    if len(result) != 0:
        app.logger.debug("USER MESSAGES RECIEVED")
        return jsonify(result), 201

    else:
        app.logger.debug("GOT NO MESSAGES")
        return jsonify(result)


@app.route('/socialmap/api/clear', methods=['POST'])
def clear():
    app.logger.debug("ATTEMPT TO CLEAR DATABASE")
    app.logger.debug(request.json)

    result = mongo_clear()
    # On success, return the user's ID
    if result == True:
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
