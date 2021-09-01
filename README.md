# WPI Planner for Workday Schedules

Based on the WPI Scheduler IQP application written by: Henrique "Nican" Polido, Ryan Anthony, Douglas Lally

Modifications by: Josh McKeen and Nick Markou

This is a modification of the data source used by the "old" WPI Planner/Scheduler application to allow the application to work with the 2021-2022 and beyond class schedules. These schedules are now created using Workday, WPI's new Student Information System which is replacing Banner/Bannerweb. 

The schedule data source is a JSON feed provided by WPI's new course listings website [courselistings.wpi.edu](https://courselistings.wpi.edu/). This version of the Planner is being hosted on Github Pages and the schedule data should automatically update approximately every 15 minutes using Github Actions. 

The live version of the planner can be found at [jmckeen8.github.io/wpiplanner/](https://jmckeen8.github.io/wpiplanner/)

The data provided by the JSON feed is converted to the XML format desired by the Planner application using a converter application I've written, which has its own repository [here](https://github.com/Jmckeen8/WorkdayToPlannerConverter). Other minor modifications have been made to the Planner application itself, namely to show seat totals by course component when viewing schedules instead of showing a single set of seat totals for an entire section.

Questions/Feedback/Bug Reports:  
Josh:  
Email: jdmckeen@wpi.edu  
Discord: Jmckeen#9470  
Reddit: u/Jmckeen8  
  
Nick:  
Email: nmmarkou@wpi.edu  
Discord: nMM456#9212  
