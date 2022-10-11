# Mauvelist

MauveList is a grey list type plugin used on the Crew Craft servers.
A grey list functions similar to a whitelist but acts more like an open house.
Any player can join but is given the rank of guest and set to spectator mode.
They are free to look around the server and decide if they want to apply. 
Once they apply, they are given the rank of member and set to survival mode.

The plugin handles the application process via two channels and DMs on Discord:
- Apply Channel - Where guests can apply to the server. *Visible to everyone.*
- Application Channel - Where moderators can review applications. *Visible to moderators only.*
- DMs - Applicant answers application questions via DM. *Visible to applicant.*

The grey list is used to prevent all the effort that goes into reading applications and accepting applicants.
I found that I was accepting players that would join once, decide the server was not for them and leave forever. 
The grey list allows potential applicants to preview the server prior to applying, saving me some time.
In addition, having the application through Discord makes it easily accessible from any platform and avoids
placing the burden of application review on a single person. It also eliminates the need for email.

[![Build Status](https://travis-ci.org/mattboy9921/MauveList.svg?branch=master)](https://travis-ci.org/mattboy9921/MauveList)
![CrewChat Made with Love](https://img.shields.io/badge/Made-with%20Love-red?&logo=undertale&logoColor=94A0A5&labelColor=384142)

**Features**
- Guests join in spectator mode, teleported to spawn.
- Discord integration for applications.
- Minecraft username validation and verification (via skin check by applicant).
- Application timeout to avoid incomplete applications.
- Prevention of creating multiple applications.
- Automatic DiscordSRV accounts linking upon acceptance. 
- Vault integration for compatibility with *most* permissions plugins
- Removes player data created by Minecraft and Essentials (optional).

**Commands**
- `/mauvelist` - Shows version.
- `/mauvelist add [player]` - Adds specified player to member list. 
- Alias: `/ml`

**Permissions**
- `mauvelist.grey` - Specifies the player as a nonmember.
- `mauvelist.admin` - Gives access to MauveList commands.

*Special thanks to Selida95.*