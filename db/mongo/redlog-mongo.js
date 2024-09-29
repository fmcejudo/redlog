const executionsCollection = 'redlogExecutions';
const reportsCollection = 'redlogReports';

function createCollectionIfNotExists(collectionName) {
    if (!db.getCollectionNames().includes(collectionName)) {
        db.createCollection(collectionName);
        print(`Created collection: ${collectionName}`);
    } else {
        print(`Collection ${collectionName} already exists`);
    }
}

function dropIndexes(collectionName) {
    const collection = db.getCollection(collectionName);
    const indexes = collection.getIndexes();
    if (indexes.length > 1) {
        collection.dropIndexes();
        print(`Dropped existing indexes on: ${collectionName}`);
    } else {
        print(`No indexes to drop for: ${collectionName}`);
    }
}

createCollectionIfNotExists(executionsCollection);
dropIndexes(executionsCollection);

db.getCollection(executionsCollection).createIndex(
    { createdAt: 1 },
    { expireAfterSeconds: 3 * 24 * 60 * 60 }
);
print(`TTL index created on ${executionsCollection} collection`);

createCollectionIfNotExists(reportsCollection);
dropIndexes(reportsCollection);

db.getCollection(reportsCollection).createIndex(
    { reportId: 1 }, // TTL index based on the reportId field
    { expireAfterSeconds: 3 * 24 * 60 * 60 } // 3 days TTL
);
print('TTL index created on redlogReports collection');
