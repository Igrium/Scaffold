# Scaffold
Scaffold is a Minecraft mapmaking toolset based on similar concepts to Source engine. It is designed to streamline the mapmaking workflow to make it easier to manage your project by breaking it up into smaller files that can be individually worked with and are compatable with [Version Control](https://git-scm.com/book/en/v2/Getting-Started-About-Version-Control).

NOTE: Because scaffold is in VERY early development, anything on this repo is subject to change or may not be functional yet. Do not use in a production envoirnment. (If you do use it in a production envoirnment, let me know how it goes!)

## How it works
Unlike many Minecraft map editors, Scaffold does not directly edit Minecraft save files. Instead, it uses a proprietary text-based format `.mclevel`, which is based on the JSON format. 

Each level contains a list of entities (not Minecraft entities) that all run special code upon compilation. Entities can be anything from a basic set of blocks to a fully functioning npc. If you can make it with a datapack (or mcedit), it can be an entity. You can also import other level files as entities for further modularity. This allows for a non-destructive workflow that can easily contain custom functionality and is compatable with Version Control.

Level files are stored inside a project folder which contains everything related to the project. Inside this project folder, along with the `maps` folder for the levels, there is an `assets` folder which contains assets that will go in the resourcepack, a `data` folder which contains functions and other data that will go in the datapack, a `scripts` folder which contains Python scripts that can be run on compilation for further customization, and a `game` folder for the Minecraft game. In the Minecraft Launcher, you should make a profile with this game folder as is game directory. This will allow Scaffold to export worlds directly into Minecraft for testing.

## Source code
Scaffold is open source, and as such, I encourage you to take the plunge and try to manipulate the code yourself. If you feel up for it, create a pull request and submit your code back to codebase.

Scaffold uses Gradle for code compilation, so to run your code, type `gradlew run` in a command prompt in the root of the Scaffold repo. Gradle will automatically handle downloading all the dependencies and compiling the code.

As of writing, there are three subsystems: `nbt`, `core`, and `editor`. Each subsystem has its own subproject in Gradle

`nbt` is lowest level of the subsystems, and is responsible for comunicating between Scaffold and low level Minecraft formats such as `.mca`.

`core` contains all the main code. For instance, level loading and compiling are handled here. It is possible to load both `core` and `nbt` as a library into other projects, as is done in `editor`.

`editor` is the highest level of the subsystems, and is responsible for interfacing with the user. It contains the GUI of Scaffold and is the main runnable project.

Scaffold also uses [JMonkeyEngine](http://jmonkeyengine.org/) as it's 3D rendering system, so you need a computer that is stronger than a calculator to run it. The minimum system requirements for JMonkeyEngine are listed [here](https://wiki.jmonkeyengine.org/jme3/requirements.html).
