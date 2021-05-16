Programming Assignments did in my Distributed Systems course

- GroupMessenger1: Designed a group messenger that can send messages to multiple AVDs and store them in a permanent key-value storage.

- GroupMessenger2: Implemented ordering guarantees to my group messenger. The guarantees are total ordering as well as FIFO ordering. As with GroupMessenger1, it store all the messages in content provider. The difference is that when storing the messages and assign sequence numbers, mechanism needs to provide total and FIFO ordering guarantees.

- SimpleDht: Designed a simple DHT based on Chord. Implemented: 1) ID space partitioning/re-partitioning, 2) Ring-based routing, and 3) Node joins.

- SimpleDynamo: Implementing a simplified version of Dynamo with 1) Partitioning, 2) Replication, and 3) Failure handling.

- SimpleMessenger: Wrote a simple messenger app on Android and enabled two Android devices to send messages to each other.
