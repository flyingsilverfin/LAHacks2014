LAHacks2014
===========
Drake Levy (draciac/drakelevy) and I started an app that intends to penalize being late to organized events.

The app is supposed to work by having the host send out invitations to various people (invitations are sent by text message using Twillio API, texts contain a code to enter into the app) they wish to invite. The text contains location and time and date as well in case the person doesn't have the app installed. Upon entering the code in the message, they are entered into the event inside the app. From there they can RSVP, and indicate that they are on their way to the event when they leave. Upon doing this, the app starts tracking their location and saves the time they arrive at the event location. Based on how early/late they are, they receive a rating which is averaged into their profile.
The host of an event can see everyone else's rating. The idea was that the host could change the time for the users with a low rating to be 5 minutes earlier or so to compensate for their habitual lateness.

Essentially, users are penalized in reputation (and others have proof that their friends are always late...)
