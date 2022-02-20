# TikTok-clone
TikTok clone written in Java(both for PC and Android versions)

Technologies used: Vanilla Java(works with 1.8) , Maven , Guava(read file , divide into chunks , etc) , Hashing MD5

Tests were made on Poco X3 NFC and on a Windows 10 computer
We have 2 clients , one running on PC and another one on the Android phone

Tabbed activity was the pre-selected interface of the AndroidApp that I went with

Below follows an in-depth explanation of the code:

Broker:

-HashingCalculator: MD5 hash , the string input is the name of the channel and we receive the hash string in hexadecimal

-Index: It's our data structure , the structure that the broker has for each client that is connected to him. The data structure is a TreeSet , meaning a tree that doesn't have 
duplicates and that is also ordered in ascending order. Each index has it's own username , meaning that each broker has a different tree for each user and also the loadFromDisk
method that reads the disk and finds all the videos that we have and inserts them into the structure.

-Video: It's the data structure that we ourselves handle , meaning the metadata from a video. Also the method compareTo() , is a method that indicates the criteria which we 
sort with , in this case based on the ID.

-BrokerData: The information of said broker , in an array of bytes that we can "work" with (Serializable indicates that this data structure has to "travel" through the internet" 
, meaning that we have to send it to a socket , and that it has to arrive as a WHOLE structure from one PC to another)

--MORE EXPLANATION TO COME--
