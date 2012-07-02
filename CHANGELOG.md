# ScavengerHunt plugin changelog summary

##1.7.0
* Changed project to maven
* Changed directory setup
* Removed worldguard support (needs fixing)
* Added optional EXP reward
* Added optional "numOfRewards" config
* Configured messages to display potion types correctly
* Removed cron4j external dependency (added cron4j source to project)
* Set thread to run only when there's a scavenger event running
* Moved most messages to a messager class. (WIP)
* Added real permissions to commands, won't use default anymore
* Replaced hardcoded command names in plugin.yml "usage:" tag with its proper macro
* If the player's inventory is full, items will be dropped on the ground next to him
* "numOf" variables now accept 0 as a valid value (i.e. numOfMobs=0 will remove mob objectives). Use -1 to disable instead
* Added optional globalNumOfObjectives(will add objectives disregarding its type). Stick to -1 for a disable
* If for any reason the final number of objectives is 0, the plugin just grants free rewards to everyone
* The event will now stop when the configs are reloaded
* Added time formatting, in XXhXXmXXs format
* Riddle mode for the new modes was left out, will fix in 1.7.1
* Source code will now be auto-formatted
* Realocation of objective check methods no longer has an ETA
* Code cleanup and small bug fixes