##Setup github account

1. global user setup
   ```
   git config --global user.email "Your email address"
   git config --global user.name "Your github account name"
   ```
2. After doing this, you may fix the identity used for this commit with:
    ```bash
    git commit --amend --reset-author
    ```
3. Adding a new SSH key to your GitHub account
    Please follow this guide to setup your local computer password less access:
    [How to adding a new ssh key to your github account](https://help.github.com/articles/adding-a-new-ssh-key-to-your-github-account/)

## Branch tips

### For Example, We have 2 branches:
* master
* ML-Flex-Modified

### How to use Branch Steps
1. Clone our stage sourcecodes to your local computer
    ```
    git clone git@github.com:Clinical3PO/Stage.git
    cd Stage
    ```
2. Make sure stay on master branch first
    ```
    git checkout master
    ```
3. Make sure all the local codes are updated from github.
    ```
    git pull
    ```
4. Switch to ML-Flex-Modified branch:
    ```
    git checkout ML-Flex-Modified
    ```
5. if you want to merge the master branch into ML-Flex-Modified, plz run:
    ```
    git rebase master
    ```
6. modify the codes
7. commit the codes
    ```
    git commit -a -m "ML-Flex changed"
    ```
8. push back to github
    ```
    git push
    ```
9. Merge into master branch if the modified ML-Flex is ready:
    ```
    git checkout master
    git merge --no-ff ML-Flex-Modified
    git push
    ```
