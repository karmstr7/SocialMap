

import configparser
import argparse
import os
import logging
logging.basicConfig(format='%(levelname)s:%(message)s',
                    level=logging.INFO)

log = logging.getLogger(__name__)
HERE = os.path.dirname(__file__)


def command_line_args():
    """Returns namespace with settings from command line"""
    log.debug("-> Command line args")
    parser = argparse.ArgumentParser(description="SocialMap")
    parser.add_argument("-D", "--debug", dest="DEBUG",
                        action="store_const", const=True,
                        help="Turn on debugging and verbose logging")
    parser.add_argument("-P", "--port", type=int, dest="PORT",
                        help="Port for Flask built-in server (only)")
    parser.add_argument("-C", "--config", type=str,
                        help="Alternate configuration file")
    cli_args = parser.parse_args()
    log.debug("<- Command line args: {}".format(cli_args))
    return cli_args


def fake_cli_args():
    """
    When we're running under a proxy like gunicorn, the command
    line belongs to the proxy and not to us, so we ignore it.  We
    create a fake, empty cli_args instead, so that we have a namespace
    with a compatible structure.
    """
    log.debug("-> Fake cli args")
    parser = argparse.ArgumentParser(description="This is a stub")
    cli_args = parser.parse_args([])
    log.debug("<- Command line args: {}".format(cli_args))
    return cli_args


def config_file_args(config_file_paths, project=None):
    """
    Returns dict of values from the configuration files,
    accessing them in the order they appear in config_file_paths.
    If the project kwarg is provided, we will take configuration
    values from that section of the configuration file if it exists,
    otherwise from DEFAULT section.
    """
    log.debug("-> config file args")
    config = configparser.ConfigParser()
    for path in config_file_paths:
        relative = os.path.join(HERE, path)
        if os.path.exists(path):
            log.info("Configuring from {}".format(path))
            config.read(path)
        elif os.path.exists(relative):
            log.info("Configuring from {}".format(relative))
            config.read(relative)
        else:
            log.info("No configuration file {}; skipping".format(path))
    section = project or "DEFAULT"
    log.debug("Using configuration section {}".format(section))
    args = config[section]
    log.debug("<- config file args: {}".format(args))
    return args


def imply_types(ns= dict):
    """
    Convert values to implied types.  We assume that strings of
    digits should be integers, and True/False (with any casing) should
    be boolean.
    """
    for var in ns:
        val = ns[var]
        if type(val) != str:
            continue
        if val.lower() == "true":
            ns[var] = True
        elif val.lower() == "false":
            ns[var] = False
        elif val.isdecimal():
            ns[var] = int(val)


def configuration(proxied=False):
    """
    Returns namespace (that is, object) of configuration
    values, giving precedence to command line arguments over
    configuration file values.

    When proxied = True, the command line is not read; all
    configuration must come from the config.ini file.  A proxy
    like gunicorn may not use some some configuration values,
    such as the PORT.
    """
    log.debug("-> configuration")
    if proxied:
        cli = fake_cli_args()
    else:
        cli = command_line_args()
    cli_vars = vars(cli)  # Access the namespace as a dict
    log.debug("CLI variables: {}".format(cli_vars))
    config_file_paths = ["app.ini", "credentials.ini"]
    if cli_vars.get("config"):
        config_file_paths.append(cli_vars.get("config"))
    log.debug("Will read config files from '{}'".format(config_file_paths))
    config_for_project = cli_vars.get("project", None)
    ini = config_file_args(config_file_paths, config_for_project)
    log.debug("Config file args: {}".format(ini))
    # Fold into cli namespace with precedence for command line arguments
    for var_lower in ini:
        var_upper = var_lower.upper()
        log.debug("Variable '{}'".format(var_upper))
        if var_upper in cli_vars and cli_vars[var_upper]:
            log.debug("Overridden by cli val '{}'".format(cli_vars[var_upper]))
        else:
            log.debug("Storing in cli")
            cli_vars[var_upper] = ini[var_lower]

    imply_types(cli_vars)

    return cli
