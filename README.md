
# SocialMap(06/05/2018):

SocialMap is an Android app. It is a social media which user can share their experiences on a map. User's friend can view his/her message on the app. It allows user to add friend, and delete a friend, add messages, delete messages and so on.
It uses [MongoDB](https://docs.mongodb.com/) as database to store events. Using [python flask](http://flask.pocoo.org/) server is used to host. Using [Android Studio](https://developer.android.com/studio/) to build the app.



## Authors:

Keir Armstrong: <karmstr7@uoregon.edu>

Quinn Milionis <qdm@uoregon.edu>  

Benjamin Michalisko: <bmichali@uoregon.edu>
 	
Shikun Lin: <shikunl@uoregon.edu> 
 	


## Getting started with back-end:

These instructions will tell you how to get a copy of the server and running the server on a local machine or a VPS(Virtual private server). The VPS is better run on a linux operating system.

### Prerequisites for back-end:  

Required to install and run the server:

 * [python3](https://www.python.org/)
 * [python3-venv](https://docs.python.org/3/tutorial/venv.html)

#### Installing Prerequisites for back-end:  

##### MACOS:  

You need to install [homebrew](https://brew.sh/) first. If you already have homebrew 
on your machine, you can skip step 1.  

```
1. /usr/bin/ruby -e "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/master/install)".  
2. brew insatll python3
3. pip3 install virtualenv 
```
		
##### Ubuntu:
```  
1. sudo apt-get install python3
2. sudo apt-get install python3-venv  
```
##### Fedora: 
``` 
sudo yum install python36  
```

 Python3.4+ include the venv module on Fedora.  


### Requirements:

This is a list of requirements for the python virtual environment. These are not required to be
manually installed, as they will be installed when you run `make install` command.

* [flask 0.12.2](http://flask.pocoo.org/)
* [gunicorn 19.7.1](http://gunicorn.org/)
* [itsdangerous 0.24](https://pythonhosted.org/itsdangerous/)
* [MarkupSafe 1.0](https://pypi.org/project/MarkupSafe/)
* [pymongo 3.6.1](https://api.mongodb.com/python/current/)
* [python-dateutil 2.7.2](https://dateutil.readthedocs.io/en/stable/)
* [six 1.11.0](https://pypi.org/project/six/)
* [Werkzeug 0.14.1](http://werkzeug.pocoo.org/)  
* [nose](http://nose.readthedocs.io/en/latest/)  

#### How to run the server:

	1. cd to the "backend" directory which contians Makefile
	2. make install
	3. make run
	4. Open the address/port listed in either the terminal or the Credentials.ini
	5. Ctrl+C to quit  

#### How to remove server:
	1. make clean
	2. make veryclean  
`make clean` should leave the project ready to run, while `make veryclean` may leave project 
in a state that requires re-running installation and configuration steps. 

## Getting started with front-end:

These instructions will tell you how to get a copy of the app and running the app on a computer or a andriod phone. 

### Prerequisites for front-end:

Required to install and run the server:

* [Android Studio](https://developer.android.com/studio/)
#### Installing Prerequisites for front-end:

Click [here](https://developer.android.com/studio/install) to see how to install Android Studio.


#### How to build the app and run:  

```
1. Go to Android Studio (https://developer.android.com/studio/) web site
to download
2. Install the Android Studio to your machine
3. Using Android Studio to  install SDK 23 or higher
4. Using Android to " Open an existing Android Studio project"
5. Choosing to open "SocialMap/frontend/SocialMap"
6. Run the project by click the "Run" on the Android Studio
7. Choosing run on virtual device or connected device
```

## Acknowledgment:

1. [Mogodb Collection Doc](https://docs.mongodb.com/manual/reference/method/js-collection/)
2. [RESTful API with Python Flask](https://blog.miguelgrinberg.com/post/designing-a-restful-api-with-python-and-flask) by Miguel Grinberg
3. [Android Developer Guides](https://developer.android.com/docs/) created by Google
