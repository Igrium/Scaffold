**Note: this is the repo for the core Scaffold code. For the editor itself, [click here.](https://github.com/Sam54123/scaffold-editor-mc)**

# Scaffold Core
Scaffold is a Minecraft mapmaking toolset based on similar concepts to Source engine. It is designed to streamline the mapmaking workflow and make it easier to manage your project by breaking it up into smaller files that can be individually worked with. It also improves compatibility with [Version Control](https://git-scm.com/book/en/v2/Getting-Started-About-Version-Control).

NOTE: Because scaffold is in VERY early development, anything on this repo is subject to change or may not be functional yet. Do not use in a production envoirnment. (If you do use it in a production envoirnment, let me know how it goes!)

## How it works
Unlike traditional Minecraft level editors, Scaffold does not directly edit Minecraft save files. Instead, it uses a proprietary xml-based format called `mclevel`.

Each level contains a list of entities (not Minecraft entities) that all run unique code upon compilation. Entities can be anything from a basic set of blocks to a fully functioning npc. If you can make it with a datapack, it can be an entity. You can also import other level files as entities for further modularity (this hasn't been implemented yet). This allows for a non-destructive workflow that can easily contain custom functionality and is compatable with Version Control.

Level files are stored inside a project folder which contains everything related to the project. Inside this project folder, along with the `maps` folder for the levels, there is an `assets` folder which contains assets that will go in the resourcepack, a `data` folder which contains functions and other data that will go in the datapack, a `scripts` folder which contains Python scripts that can be run on compilation for further customization, and a `game` folder for the Minecraft game itself. In the Minecraft Launcher, you should make a profile with this game folder as its game directory. This will allow Scaffold to export worlds directly into Minecraft for testing.

## Source code
Scaffold is open source, and as such, I encourage you to take the plunge and try to manipulate the code yourself. If you feel up for it, create a pull request and submit your code back to codebase.

Scaffold uses Gradle for code compilation, so to build your code, type `gradlew build` in a command prompt in the root of the Scaffold repo. Gradle will automatically download all the dependencies and compilie the code.

As of writing, there are three subsystems: `scaffold-nbt`, `scaffold-core`, and the editor. Each subsystem has its own Gradle project.

`scaffld-nbt` is lowest level of the subsystems. It is responsible for comunicating between Scaffold and low-level Minecraft formats such as `.mca`.

`scaffold-core` contains all the main code. Level loading and compiling are handled here. It is possible to include both `core` and `nbt` as libraries into other projects, as is done in the editor.

The editor is the highest-level subsystem and is implemented as a Fabric mod for Minecraft. You can find it [here](https://github.com/Sam54123/scaffold-editor-mc).
