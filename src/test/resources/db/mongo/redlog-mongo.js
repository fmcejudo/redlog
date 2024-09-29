print("start execution redlog-mongo script")

db = db.getSiblingDB('admin');
db.auth('root', 'pass');

db = db.getSiblingDB("redlog");

db.createUser({
    user: 'test',
    pwd: 'test',
    roles: [
        { role: 'readWrite', db: 'redlog' }
    ]
});

db.createCollection('testExecutions');
print('created collection testExecutions')
db.createCollection('testReports');
print('created collection testReports')
db.testExecutions.createIndex(
    { createdAt: 1 },
    { expireAfterSeconds: 10 }
);
db.testReports.createIndex(
    { createdAt: 1 },
    { expireAfterSeconds: 10 }
);
print('TTL index created on redlogReports collection');
print('redlog collections created');