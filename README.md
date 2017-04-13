#FAIRifier

Depenencies:
  - Java 8
  - npm
  - gulp
  - bower
  - Apache Ant


Building
========
install depedencies (assuming Java 8 is installed)

```
sudo apt-get install nodejs-legacy npm ant

```
install gulp
```
sudo npm install -g gulp

```
install bower
```
sudo npm install -g bower

```
download the git repository
```
git clone --recursive -b development https://github.com/DTL-FAIRData/FAIRifier.git
```
cd to the directory of the git repo
```
cd FAIRifier/
```
and now build
```
ant build
```

Running
==========
Run the ./refine file
```
./refine
```

Instructions
============

Because the FAIRifier is based on OpenRefine it has all the functionalities of 
OpenRefine. The main added functionality is to add the FAIRified data as RDF to 
a data resource (FTP or virtuoso tripple store - more to be added later). 
This allows the user to push FAIR data to a resource and metadata to a FAIRDataPoint(FDP)
with one application. The forms used to specify the metadata are the same as [the metadata editor](https://github.com/DTL-FAIRData/FAIR-metadata-editor/tree/develop).
To access this new function first prepare the dataset to contain the RDF you want.
You also need to have upload rights to a FDP. To push the data you need access 
to a tripple store or a FTP.
Afterwards press the POST to FAIRDataPoint option in the Export menu.

![alt tag](https://raw.githubusercontent.com/Shamanou/FAIRifier/development/git_tutorial_images/tutorial-1.png "Press the POST to FAIRDataPoint option in the Export menu")

Clicking the POST to FAIRDataPoint option will open the POST to Fair Data Point dialog.
In the Base URI field requires you to fill in a the URL of the FDP where you want
to store the metadata. Please fill in the complete url to the root of the FDP API.

![alt tag](https://raw.githubusercontent.com/Shamanou/FAIRifier/development/git_tutorial_images/tutorial-2.png "Please fill in the complete url to the root of the FDP API.")

When you click on the Apply button the catalogs menu will show up in the dialog.
It will show a + with the text add catalog and a drop down menu which, when using 
a non-empty FDP, will show all the catalogs in the FDP. If the FDP is empty the
drop down menu will be empty. Click on the + to add a new catalog.

![alt tag](https://raw.githubusercontent.com/Shamanou/FAIRifier/development/git_tutorial_images/tutorial-3.png "Click on the + to add a new catalog.")

This will open the add new catalog dialog. Fill in this form, to add the metadata to the new catalog
layer to the FDP.

![alt tag](https://raw.githubusercontent.com/Shamanou/FAIRifier/development/git_tutorial_images/tutorial-4.png "This will open the add new catalog dialog.")

When you click save You will return to the POST to FAIR Data Point dialog and a new 
field will apear. This field will show a + add dataset and a dropdown menu. 
The dropdown menu will list the datasets within the selected catalog, if the 
catalog is empty or new the dropdown menu will be empty.

![alt tag](https://raw.githubusercontent.com/Shamanou/FAIRifier/development/git_tutorial_images/tutorial-5.png "When you click save You will return to the POST to FAIR Data Point dialog and a new field will apear.")

When you click the + button the Add new dataset to FAIR Data Point will apear. Fill in this form, to add the metadata to the new dataset layer to the FDP.

![alt tag](https://raw.githubusercontent.com/Shamanou/FAIRifier/development/git_tutorial_images/tutorial-6.png "When you click the + button the Add new dataset to FAIR Data Point will apear.")

When you click save You will return to the POST to FAIR Data Point dialog and a new 
field will apear. This field will show a + add distribution and a dropdown menu. 
The dropdown menu will list the distributions within the selected catalog, if the dataset 
doesnt have any distributions the dropdown menu will be empty.

![alt tag](https://raw.githubusercontent.com/Shamanou/FAIRifier/development/git_tutorial_images/tutorial-7.png "When you click save You will return to the POST to FAIR Data Point dialog and a new field will apear.")

This will open the add new distribution dialog. Fill in this form, to add the metadata to the new distribution layer to the FDP.

![alt tag](https://raw.githubusercontent.com/Shamanou/FAIRifier/development/git_tutorial_images/tutorial-8.png "This will open the add new distribution dialog.")

When you click save You will return to the POST to FAIR Data Point dialog and a new 
field will apear. This field contains the data resource options. For now FAIR data can be directly put on a FTP or on a Virtuoso
tripplestore. 

![alt tag](https://raw.githubusercontent.com/Shamanou/FAIRifier/development/git_tutorial_images/tutorial-9.png "When you click save You will return to the POST to FAIR Data Point dialog and a new field will apear.")

To upload the FAIR data to Virtuoso select the radio button push FAIRIfied data to Virtuoso.

![alt tag](https://raw.githubusercontent.com/Shamanou/FAIRifier/development/git_tutorial_images/tutorial-10.png "To upload the FAIR data to Virtuoso select the radio button push FAIRIfied data to Virtuoso.")

The host should be the Virtuso hostname and the path.
Usually this is: <hostname>/DAV/home/<username>

NOTE: localhost as a string is not accepted. Please use 127.0.0.1

If everything is filled in correctly then you should see the uploading animation and when uploading
is done a dialog should apear with the text FAIR data pushed.

![alt tag](https://raw.githubusercontent.com/Shamanou/FAIRifier/development/git_tutorial_images/tutorial-11.png "If everything is filled in correctly then you should see the uploading animation and when uploading is done a dialog should apear with the text FAIR data pushed.")

