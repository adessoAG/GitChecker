# Git Checker
The Git Checker is a Tool for checking the following of individually defined conventions in dealing with the Version Control System Git.

Those conventions are meant to be based on the [Git-Flow](https://nvie.com/posts/a-successful-git-branching-model/) workflow where there are different types of branches based on their responsibility.

<img src="./docs/git-flow.png" alt="Git-Flow" width="400"></img>

At the moment the Tool is only designed for repositories hosted on BitBucket servers.

## Possible Checks
* Branch naming patterns
    * Used to assign branchtypes
* Allowed origins of branchtypes
* Allowed merge targets of branchtypes
* Stale branches
* Unremoved branches after pull request merge

## Usage

### Create configuration file
At first you need to create a configuration file in .json format.
This file contains information about the target resource as well as the rules the tool is going to check.

An example of the configuration file looks like this:
```json
{
  "resources": {
    "serverURL": "https://bitbucket.com",
    "project": "PROJECTKEY",
    "targetRepository": "backend"
  },

  "rules": {
    "branchNamingPatterns": {
      "MASTER": "^master$",
      "DEVELOP": "^dev$",
      "FEATURE": "feature\/TKT-[0-9]{1,4}-[a-zA-Z\\-]+",
      "BUGFIX": "bugfix\/TKT-[0-9]{1,4}-[a-zA-Z\\-]+",
      "DEVELOPER": "task\/TKT-[0-9]{1,4}-[a-zA-Z\\-]+"
    },
    "allowedBranchOrigins": {
      "DEVELOP": ["MASTER"],
      "FEATURE": ["DEVELOP"],
      "BUGFIX": ["MASTER", "DEVELOP"],
      "DEVELOPER": ["FEATURE"]
    },
    "allowedBranchMerges": {
      "DEVELOP": ["MASTER"],
      "FEATURE": ["DEVELOP"],
      "BUGFIX": ["MASTER", "DEVELOP"],
      "DEVELOPER": ["ORIGIN"]
    },
    "branchStalePeriods": {
      "FEATURE": 90,
      "BUGFIX": 14,
      "DEVELOPER": 30
    },
    "branchRemovalAfterPRMerge": true
  }
}
```
Please note that you can only use branchtypes you previously declared in the "branchNamingPatterns" section!

An exception to this is the value "ORIGIN" you can use in the "allowedBranchMerges" section to allow merges into the branch of origin.

### Run the tool
To run the program you can either have the config file in the same directory as the tool or pass the location of it as an argument with ```--configfile=/path/to/configfile.json```.

Besides to the configfile you need to pass credentials for accessing the repository as an command line argument.

Optionally you can pass arguments to the application to control the amount of fetched elements per request.
To alter the number of fetched commits (default is 1000) pass the argument ```--webclient.requestSize.commits=2000```.
The number per request of every other element (branches, pull requests...) (default is 50) is controlled by ```--webclient.requestSize=100```.

Please note that the size of the codecs buffer may not be enough to handle very high numbers of elements.
You can change the buffersize by the argument ```--webclient.codec.buffersize=4```, where the number means the size in MB (default is 2).

Every parameter example:
```java -jar git-checker-1.0.0.jar --user=Foo --password=Bar --configfile=./Baz.json --webclient.requestSize.commits=2000 --webclient.requestSize=100 --webclient.codec.buffersize=4```

## Results
The output is grouped by the type of rule violation:
```text
ILLEGAL_MERGE
Merge of bugfix/TKT-42-parsing-error into feature/TKT-37-expand-parser was not allowed.

BRANCH_UNREMOVED
Branch bugfix/TKT-42-parsing-error was not removed after pull request merge.

BRANCH_STALE
Branch feature/TKT-34-file-upload was not updated since 35 days.

ILLEGAL_NAMING
No pattern matches branch: refactor/code-aligning
No pattern matches branch: TKT-45-parsing-events
```

## Return codes
| Code | Meaning                                            |
| :--- | :------------------------------------------------- |
| 0    | Repository is fine                                 |
| 1    | Configfile not recognized                          |
| 2    | Ruleset couldn't be parsed from configfile         |
| 3    | Configfile is inconsistent                         |
| 4    | Target resource couldn't be parsed from configfile |
| 5    | Project couldn't be loaded from Server             |
| 6    | Repository couldn't be loaded from Server          |
| 7    | Branches couldn't be loaded from Server            |
| 8    | Pull Requests couldn't be loaded from Server       |
| 9    | Commits couldn't be loaded from Server             |
| 10   | Parsed rules being broken                          |
| 11   | Authentication failed                              |
