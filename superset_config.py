# superset_config.py
import os

# Superset specific config
ROW_LIMIT = 5000
SUPERSET_WEBSERVER_PORT = 8088

# Flask App Builder configuration
# Your App secret key
SECRET_KEY = 'thisISaSECRET_key'

# The SQLAlchemy connection string to your database backend
# This example uses SQLite, which is built into Python
SQLALCHEMY_DATABASE_URI = 'sqlite:////app/superset_home/superset.db'

# Flask-WTF flag for CSRF
WTF_CSRF_ENABLED = True

# Add additional connections via environment variables
SQLALCHEMY_EXAMPLES_URI = 'sqlite:////app/superset_home/examples.db'

# The authentication type
# AUTH_OID : Is for OpenID
# AUTH_DB : Is for database (username/password)
# AUTH_LDAP : Is for LDAP
# AUTH_REMOTE_USER : Is for using REMOTE_USER from web server
AUTH_TYPE = 1

# Enable access to examples
SUPERSET_LOAD_EXAMPLES = True