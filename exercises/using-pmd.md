# Using PMD

Pick a Java project from Github (see the [instructions](../sujet.md) for suggestions). Run PMD on its source code using any ruleset. Describe below an issue found by PMD that you think should be solved (true positive) and include below the changes you would add to the source code. Describe below an issue found by PMD that is not worth solving (false negative). Explain why you would not solve this issue.

## Answer

There is an error that states 

`SerializationUtilsTest.java:367:	UnusedFormalParameter:	Avoid unused method parameters such as 'in'.`

There is a parameter that is declared and not used afterwards. It should be fixed because it uses memory pointlessly :

```
private void readObject(final ObjectInputStream in) throws ClassNotFoundException    {

    throw new ClassNotFoundException(SerializationUtilsTest.CLASS_NOT_FOUND_MESSAGE);

}
```


To fix it we remove the parameter from the method definition

```
private void readObject() throws ClassNotFoundException    {

    throw new ClassNotFoundException(SerializationUtilsTest.CLASS_NOT_FOUND_MESSAGE);

}
```

As for a false positive we have this error :


`ThreadUtilsTest.java:20:	TooManyStaticImports:	Too many static imports may lead to messy code`

which warns the user that there is a lot of import and that it may not be readable. This is a minor problem because it would not prevent compilation and just hinders readability therefore, does not need to be fixed. There is also a lot of false positive that say that there are useless parenthesis.

`StringEscapeUtilsTest.java:141:	UselessParentheses:	Useless parentheses.`
