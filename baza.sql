DROP TABLE IF EXISTS grad;
DROP TABLE IF EXISTS drzava;
CREATE TABLE drzava(drzavaID int, naziv text, glavni_grad int, primary key (drzavaID), foreign key (glavni_grad) references grad (gradID));
CREATE TABLE grad(gradID int, naziv text, broj_stanovnika int, drzava int, slikaPath text, primary key (gradID), foreign key (drzava) references drzava (drzavaID));
INSERT INTO grad VALUES(1, 'Pariz', 2206488, 1, 'file:/C:/Users/Lenovo/Desktop/download.jpg');
INSERT INTO grad VALUES (2, 'London', 8825000, 2, 'file:/C:/Users/Lenovo/Desktop/london.jpg');
INSERT INTO grad VALUES (3, 'Beč', 1899055, 3, 'file:/C:/Users/Lenovo/Desktop/bec.jpg');--Pazi vazan je redoslijed kako unosis, to dobro pazi, racunar je glup
INSERT INTO grad VALUES (4, 'Manchester', 545500, 2, 'file:/C:/Users/Lenovo/Desktop/mancester.jpg');
INSERT INTO grad VALUES (5, 'Graz', 280200, 3, 'file:/C:/Users/Lenovo/Desktop/download.jpg');
INSERT INTO drzava VALUES (1, 'Francuska', 1);
INSERT INTO drzava VALUES (2, 'Velika Britanija', 2);
INSERT INTO drzava VALUES (3, 'Austrija', 3);