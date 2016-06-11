# Project pieces of ezHome Project #

The ezHome Project is composed of many pieces that have to work together to the main goal. This pieces involves many different technologies so its necessary to define the bounds for each one.

This pieces are:

* **[Controller](controller/README.md)**: The main coordinator of the project, is responsible for connected every other parts. Is developed in Java SE;
* **[Local configuration site](local-site/README.md)**: Run over the controller. Is the local user interface for ezHome instalation and configuration. Is developed on Angular 2 with angular-material2. As the backend runs over the controller, it is developed on Java SE;
* **[Devices](devices/README.md)**: Are the electrical parts of the project. Mainly are used Arduinos for the job, but other device types can be accepted;
* **[Remote controlling site](remote-site/README.md)**: Is the remote web-site responsible for controlling and monitoring the home. It also controls activation and remote backups of configurations and communications with mobile platforms. It is develop on PHP and stored with postgresql;
* **[Mobile App](mobile/README.md)**: Mobile aplication for remote controlling the Home. Can communicate with remote controlling site or local configuration site, depending on used network. Is developed with ionic2;

As the project is on the beginning, the technologies are only suggestions and can change if there is a good reason for it.
