db = db.getSiblingDB('aggregator');

db.createUser({
  user: 'dev_mongo',
  pwd: 'dev_password',
  roles: ['readWrite'],
});
