Real Time Bidding Engine
This program implements a Real Time Bidding engine with 2 layers of cache.

Functionality
Anonymous User Registration:

Anonymous users are registered in the users database with new attributes.
Existing User Update:

Existing users can update their data in the database.
Campaign Request Handling:

When a user requests a campaign, the system checks the local service cache for the campaign queue.
If not found, it looks in the external Redis data implemented as a TreeMap, along with extensive Redis Service real methods.
If the campaign is not found in Redis, it retrieves the data from the users database.
Scheduled Tasks:

Every 6 hours, a complete campaign match is performed between the users database and campaigns database, updating Redis accordingly.
Every 1 hour, the local cache fetches the most accessed users from Redis.
Every 3 hours, Redis cache eviction is performed to make room for new users.
Multi-threaded User-Campaign Matching:

The major operation of matching users to campaigns offline is implemented using multi-threaded Java 8 streams.
Whitelist and Blacklist:

Whitelist: Priority queue that sorts campaigns immediately upon insertion based on priority parameters.
Blacklist: Holds each user's campaigns to be ignored in future matches.
Implementation Details
The program is developed using Spring Boot, a Java-based framework for building robust and scalable applications.
The caching mechanism includes a two-layered approach: local service cache and external Redis cache.
Scheduled tasks are managed using Spring's scheduling features.
Multi-threading and parallel processing are utilized for efficient user-campaign matching.
Whitelist and blacklist functionalities help in prioritizing and filtering campaigns.
