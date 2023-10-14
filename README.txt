This program is a Real Time Bidding engine with 2 layers of cache implimented

1. anonynous user will be registered to the users DB with new attributes
2. existing user will just update his data to the DB
3. user request for campaign will initially check the local service cache for campaign queue
4. if not found it will turn to the external Redis data immplimented here as TreeMap but also with exytensive Redis Service real methods
5. if not found in redis then forced to get data from the users DB

6. schedulaed tasks are: 
- every 6 hours complete campaign match between users DB and campaigns DB and by that also updating the Redis
- every 1 hour the local cache is taking the most hits users from the Redis
- every 3 hours Redis Cache eviction will make room for new users

7. the major operation of matching users to campaigns offline is multi threaded java8 streams immplimented
8. whitelist is a priority queue which sorts the campaigns immidiatly upon insertion based on the priority parameters
9. blacklist will hold each users campaigns to be ignored from future match

