db = db.getSiblingDB('characters');

db.createUser({
  user: 'test_container',
  pwd: 'test_container',
  roles: [
    { role: 'readWrite', db: 'characters' }
  ]
});

db.characters.insertOne({
  "name": "Luke Skywalker",
  "affiliation": "Rebel Alliance",
  "role": "Jedi Knight",
  "species": "Human"
});

db.characters.insertOne({
  "name": "Princess Leia Organa",
  "affiliation": "Rebel Alliance",
  "role": "Princess, Diplomat",
  "species": "Human"
});

db.characters.insertOne({
  "name": "Han Solo",
  "affiliation": "Rebel Alliance",
  "role": "Smuggler, Pilot",
  "species": "Human"
});

db.characters.insertOne({
  "name": "Chewbacca",
  "affiliation": "Rebel Alliance",
  "role": "Co-pilot, Warrior",
  "species": "Wookiee"
});

db.characters.insertOne({
  "name": "C-3PO",
  "affiliation": "Rebel Alliance",
  "role": "Protocol Droid",
  "species": "Droid"
});

db.characters.insertOne({
  "name": "R2-D2",
  "affiliation": "Rebel Alliance",
  "role": "Astromech Droid",
  "species": "Droid"
});

db.characters.insertOne({
  "name": "Mon Mothma",
  "affiliation": "Rebel Alliance",
  "role": "Senator, Leader",
  "species": "Human"
});

db.characters.insertOne({
  "name": "Lando Calrissian",
  "affiliation": "Rebel Alliance",
  "role": "Smuggler, Gambler",
  "species": "Human"
});

db.characters.insertOne({
  "name": "Wedge Antilles",
  "affiliation": "Rebel Alliance",
  "role": "Pilot",
  "species": "Human"
});

db.characters.insertOne({
  "name": "Jyn Erso",
  "affiliation": "Rebel Alliance",
  "role": "Rebel Leader",
  "species": "Human"
});

// Imperial Characters
db.characters.insertOne({
  "name": "Darth Vader",
  "affiliation": "Galactic Empire",
  "role": "Sith Lord",
  "species": "Human"
});

db.characters.insertOne({
  "name": "Emperor Palpatine",
  "affiliation": "Galactic Empire",
  "role": "Emperor, Sith Lord",
  "species": "Human"
});

db.characters.insertOne({
  "name": "Boba Fett",
  "affiliation": "Bounty Hunter",
  "role": "Bounty Hunter",
  "species": "Human"
});

db.characters.insertOne({
  "name": "Grand Moff Tarkin",
  "affiliation": "Galactic Empire",
  "role": "Grand Moff, Military Leader",
  "species": "Human"
});

db.characters.insertOne({
  "name": "Darth Maul",
  "affiliation": "Galactic Empire",
  "role": "Sith Lord",
  "species": "Dathomirian Zabrak"
});

db.characters.insertOne({
  "name": "Count Dooku",
  "affiliation": "Galactic Empire, Separatists",
  "role": "Sith Lord, Count",
  "species": "Human"
});

db.characters.insertOne({
  "name": "Admiral Thrawn",
  "affiliation": "Galactic Empire",
  "role": "Admiral",
  "species": "Chiss"
});

db.characters.insertOne({
  "name": "Imperial Officer",
  "affiliation": "Galactic Empire",
  "role": "Officer",
  "species": "Human"
});

db.characters.insertOne({
  "name": "Stormtrooper",
  "affiliation": "Galactic Empire",
  "role": "Soldier",
  "species": "Human"
});

db.characters.insertOne({
  "name": "Director Krennic",
  "affiliation": "Galactic Empire",
  "role": "Director of Advanced Weapons Research",
  "species": "Human"
});
