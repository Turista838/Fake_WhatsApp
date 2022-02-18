# Fake_WhatsApp

Project developed for 'Distributed Programming', a third year subject @ISEC

## Features:

* Registration of new users
* User authentication
* Edit user data
* List available users
* Establishment of contact between users
* Send messages and files
* Contact list view
* Delete a contact
* Group creation
* Group admin can edit/erase group
* Group list view
* Delete messages
* Asynchronous (and automatic) update of the information viewed by users

## Manual:

* Libraries needed: JBDC and JavaFX
1. Clone project and create dabase using db_schema.sql
2. Start GRDS fisrt, then Server (1 or more), and only then Clients (1 or more)
* GRDS arguments - Port that accepts connections
* Server arguments - IP and Port of GRDS
* Client arguments - IP and Port of GRDS
* You also need to change the database connection string of Server (String dbUrl)

## Communications Schema:

![Communications Schema](https://i.imgur.com/cAmQVIQ.png)
