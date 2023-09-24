# WPI Planner for Workday Schedules

For implementation: use contents of the "war" folder

This repository hosts the actively maintained version of the WPI Planner, which has been modified to display schedule data from Workday instead of Banner. 

The version of Planner in this repository works in conjunction with the ["WorkdayToPlannerConverter"](https://github.com/Jmckeen8/WorkdayToPlannerConverter) tool, which generates the necessary "new.schedb" file. Other minor modifications have been made to the Planner application itself, including additional color coding to indicate course availability, warning symbols for closed/waitlisted courses, sharable schedule links, and favorited schedules that persist between sessions. 

The original schedule data source is a [JSON output](https://courselistings.wpi.edu/assets/prod-data-raw.json) of the "Course For Rainbow Sheets 2.0 - WPI" Workday report, which is also used by WPI's public [Course Listings website](https://courselistings.wpi.edu/). 

The deployed production version of the WPI Planner may be found at https://planner.wpi.edu/.

The development version may be found at https://jmckeen8.github.io/wpiplanner/.

Based on the WPI Scheduler IQP application written by: Henrique "Nican" Polido, Ryan Anthony, Douglas Lally:<br>
[GitHub Repository](https://github.com/Nican/wpischeduler)<br>
[Digital WPI Project Listing](https://digital.wpi.edu/show/zw12z591f)

Modifications by: [Jordyn McKeen](https://github.com/Jmckeen8) and [Nick Markou](https://github.com/NicholasMarkou)

This web application is currently maintained by WPI Information Technology Services. For support, questions, or feedback please contact [WPI ITS](https://hub.wpi.edu/Help).
