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
    app.logger.debug("ATTEMPT TO LOG IN USER")

    username = request.json['username']
    password = request.json['password']

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
    app.logger.debug("ATTEMPT TO CREATE USER")

    username = request.json['username']
    password = request.json['password']
    date = request.json['date_created']

    act = {
        'username': username,
        'password': password,
        'date_created': date
    }

    result = mongo_signup(act)

    if result['error_msg'] is not "":
        app.logger.debug('ERROR DURING SIGNUP: {}'.format(result['error_msg']))
    else:
        app.logger.debug('CREATED NEW ACCOUNT: {}'.format(result['username']))

    return jsonify(result), 200


@app.route('/socialmap/api/addMsg', methods=['POST'])
def addMsg():
    app.logger.debug("ATTEMPT TO ADD MSG")

    username = request.json['username']
    msg_data = request.json['msg_data']
    msg_body = request.json['msg_body']

    msg = {
        "msg_data": msg_data,
        "msg_body": msg_body
    }

    result = mongo_addMsg(username, msg)

    if result['error_msg'] is not "":
        app.logger.debug('ERROR DURING ADD MESSAGE: {}'.format(result['error_msg']))
    else:
        app.logger.debug('ADDED NEW MESSAGE: {}'.format(result['msg_body']))

    return jsonify(result), 200


@app.route('/socialmap/api/delMsg', methods=['POST'])
def delMsg():
    app.logger.debug("ATTEMPT TO DELETE MSG")

    message_id = request.json['message_id']

    result = mongo_delMsg(message_id)

    if result['error_msg'] is not "":
        app.logger.debug('ERROR DURING DELETE MESSAGE {}: {}'.format(message_id, result['error_msg']))
    else:
        app.logger.debug('DELETED MESSAGE {}'.format(message_id))

    return jsonify(result), 200


@app.route('/socialmap/api/addFriend', methods=['POST'])
def addFriend():
    app.logger.debug("ATTEMPT TO ADD FRIEND")

    username = request.json['username']
    friend = request.json['friend']         # username of the friend

    result = mongo_addFriend(username, friend)

    if result['error_msg'] is not "":
        app.logger.debug('ERROR DURING ADDING {} TO {}: {}'.format(username, friend, result['error_msg']))
    else:
        app.logger.debug('ADDED {} AS FRIEND TO {}'.format(friend, username))

    return jsonify(result), 200


@app.route('/socialmap/api/delFriend', methods=['POST'])
def delFriend():
    app.logger.debug("ATTEMPT TO DELETE FRIEND")

    username = request.json['username']
    friend = request.json['friend']

    result, error_msg = mongo_delFriend(username, friend)

    if result['error_msg'] is not "":
        app.logger.debug('ERROR DURING DELETING FRIEND {} FROM USER {}: {}'.format(friend, username, result['error_msg']))
    else:
        app.logger.debug('DELETED {} AS FRIEND FROM {}'.format(friend, username))

    return jsonify({result}), 200


@app.route('/socialmap/api/getFriends', methods = ['POST'])
def getFriends():
    app.logger.debug("ATTEMPT TO GET FRIENDS")

    username = request.json['username']

    result = mongo_getFriends(username)

    if result['error_msg'] is not "":
        app.logger.debug('ERROR DURING GETTING FRIENDS FOR {}: {}'.format(username, result['error_msg']))
    else:
        app.logger.debug('GOT FRIEND LIST FOR {}'.format(username))

    return jsonify(result), 200


@app.route('/socialmap/api/delUser', methods=['POST'])
def delUser():
    app.logger.debug("ATTEMPT TO DELETE USER")

    user_id = request.json['user_id']

    result = mongo_delUser(user_id)

    if result['error_msg'] is not "":
        app.logger.debug('ERROR DURING DELETING USER {}: {}'.format(user_id, result['error_msg']))
    else:
        app.logger.debug('DELETED USER {}'.format(user_id))

    return jsonify(result), 200


@app.route('/socialmap/api/getMsgs', methods=['POST'])
def getMsgs():
    app.logger.debug("ATTEMPT TO GET MESSAGES")

    username = request.json['username']
    friends = request.json['friends']

    result, error_msg = mongo_getMsgs(username, friends)

    print(result)

    app.logger.debug("ERROR DURING GETMSGS: {}".format(error_msg))
    return jsonify({"result": result, "error_msg": error_msg}), 200


@app.route('/socialmap/api/clear', methods=['POST'])
def clear():
    app.logger.debug("ATTEMPT TO CLEAR DATABASE")

    result = mongo_clear()
    # On success, return the user's ID
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
