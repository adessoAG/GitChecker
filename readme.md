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
      "MASTER": "master",
      "DEVELOP": "dev",
      "FEATURE": "feature\/{1,4}[a-zA-Z\\-]+",
      "BUGFIX": "bugfix\/{1,4}[a-zA-Z\\-]+",
      "DEVELOPER": "task\/{1,4}[a-zA-Z\\-]+"
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
Example:
```java -jar git-checker-1.0.0.jar --user=Foo --password=Bar --configfile=./Baz.json```

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
