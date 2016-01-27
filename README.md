# Stage

Setup github account
>git config --global user.email "Your email address"
>git config --global user.name "Your github account name"

Make sure stay on master branch first

>git checkout master

If master branch has commits:

>git push --set-upstream origin ML-Flex-Modified

Switch to ML-Flex-Modified branch:

>git checkout ML-Flex-Modified
modify the codes
commit the codes
> git commit -a -m "ML-Flex changed"
> git push ML-Flex-Modified

Merge into master branch if the modified ML-Flex is ready:
git checkout master
git merge --no-ff ML-Flex-Modified





