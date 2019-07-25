MauveList is a grey list type plugin used on the Crew Craft servers.
A grey list functions similar to a whitelist but acts more like an open house.
Any player can join but is given the rank of nonmember and set to spectator mode.
They are free to look around the server and decide if they want to apply. 
Once they apply, they are given the rank of member and set to survival mode.

The grey list is used to prevent all the effort that goes into reading applications and accepting applicants.
I found that I was accepting players that would join once, decide the server was not for them and leave forever. 
The grey list allows potential applicants to preview the server prior to applying, saving me some time.

[![Build Status](https://travis-ci.org/mattboy9921/MauveList.svg?branch=master)](https://travis-ci.org/mattboy9921/MauveList)

**Features**
- Allows nonmembers to join in spectator mode
- Tracks last 10 nonmembers
- Easily add members to server
- Vault integration for compatibility with *most* permissions plugins
- Removes player data created by Minecraft and Essentials (if present).

**Commands**
- `/mauvelist` - Shows version.
- `/mauvelist reload` - Reloads the plugin configuration.
- `/mauvelist list` - Shows last 10 joined nonmembers.
- `/mauvelist add [player]` - Adds specified player to member list. 
- Alias: `/ml`

**Permissions**
- `mauvelist.grey` - Specifies the player as a nonmember.
- `mauvelist.admin` - Gives access to MauveList commands.