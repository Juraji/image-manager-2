# Image Manager 2
It's like Pinterest Downloader (2) and Image Manager, but better!

## Installation

### Requirements
* Java 8 or higher  
_That's it really_

### Getting started
1. Download the latest version of the Image Manager JAR from [releases](https://github.com/Juraji/image-manager-2/releases).
2. Place the JAR file in a safe location.  
  _It will generate some files around it, like a database, log files etc._
3. Start Image Manager either by  
  a. Double-clicking the JAR file (Which should have Java open and start it for you).  
  b. Opening your OS's terminal, browsing to the directory in which you placed the JAR file and issuing  
  `$ java -jar image-manager-2.x.x.jar`

## Usage
> I will implement a wiki for this, if people need it.  
> Until then I hope the following will suffice.

### Menu Run-down
> Items with a "*" require Pinterest login information to be set within the settings.

* __System__
  * __Settings__  
  Change Settings like the default directory for backups and Pinterest login information.
  * __View logs__  
  View system logs, for trouble shooting purposes.
  * __Exit__  
  Exit the application.
* __Directories__
  * __Add local directory__  
  Add a local directory, containing image files.
  * *__Add Pinterest board__  
  Add boards from Pinterest.  
  * __Index all directories__  
  Check for changes in local and Pinterest directories.  
  This will optionally download, file and hash new images.  
  _When one or more of your directories is a Pinterest board, Image Manager will ask if it should scan latest pins or the entire board._
  * __Index favorite directories__  
  Check for changes in directories marked as favorite
  _You can set favorite boards and directories by right-clicking the directory and selecting "Toggle favorite"._  
  _When one or more of your favorite directories is a Pinterest board, Image Manager will ask if it should scan latest pins or the entire board._
  * __Open target directory__  
  Open the target directory, set within the settings, in your OS's file browser.
* __Tools__
  * __Duplicate Scanner__  
  This is a fun little tool, almost<sup>TM</sup> perfectly Picks out duplicates in a single directory or across all directories!
  * __Generate missing hashes__  
  Image Manager generates hashes during indexation of directories.  
  _You shouldn't need this, unless you use the nex option. :D_
  * __Delete all hashes__ 
  Delete all generated hashes.  
  _Use the previous option in case you accidentally clicked this!_

### Some quick start actions
* __Double-click a directory in the main window to open up an image view.__  
  From here you can view, move and delete indexed images.  
  Yes that's right, you can move Pinterest pins between boards from here!
  _Note that it's currently not possible to move Pinterest pins to "local" directories or the other way around._  
  _Also it's not possible to upload new pins, The Pinterest website still needs a purpose!_
* __Most UI items have context menus.__  
  Try right-clicking things to find out what you can do with it.
