[![Codacy Badge](https://api.codacy.com/project/badge/Grade/d122f3edbb6e4ebfa8c74c4b219ce3a2)](https://www.codacy.com/app/Shamanou/FAIRifier?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=Shamanou/FAIRifier&amp;utm_campaign=Badge_Grade)

The FAIRifier is a tool to make messy data [FAIR](https://www.dtls.nl/fair-data/).

The FAIRifier is a tool to make data FAIR(Findable, Acessable, Interoperable and Reusable). 
The FAIRifier is developed at [DTL](https://www.dtls.nl/)(the Dutch Tech Centre for life sciences) and should be used if a user wants
to make non-FAIR datasets FAIR and push the metadata to a [FDP](https://github.com/DTL-FAIRData/FAIRDataPoint)(Fair Data Point).

Dependencies:
  - Java 8
  - Apache Ant


Building
========
install depedencies (assuming Java 8 is installed)

```
sudo apt-get install ant
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
./refine build
```

Running
==========
Run the ./refine file
```
./refine
```
