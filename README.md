# CraftingLib
Simple library for creating custom craftings.

### Eternal Repository
```xml
<repository>
  <id>eternalcode-repository</id>
  <name>EternalCode.pl</name>
  <url>https://repo.eternalcode.pl/releases</url>
</repository>
```
```groovy
maven {
    url "https://repo.eternalcode.pl/releases"
 }
```

### Dependencies
```xml
<dependency>
  <groupId>net.osnixer</groupId>
  <artifactId>craftinglib</artifactId>
  <version>1.0</version>
</dependency>
```
```groovy
implementation 'net.osnixer:craftinglib:1.0'
```

### How to use?
You need to apply your plugin instance to library.
```java
CraftingLib craftingLib = new CraftingLib(instance);
```

Getting CraftingManager
```java
CraftingManager craftingManager = craftingLib.getManager();
```

## CraftingManager

**CraftingManager#createCrafting**
> It is a method that creates a crafting with a given name and a Crafting object.
> ***Remember that the name can't be repeated***

—

**CraftingManager#removeCrafting**
> It is a method that removes a crafting with a given name.


—


**CraftingManager#findCrafting - returning Option<Crafting>**
> It is a method that find a crafting with a given name or ItemStack.


—


**CraftingManager#get - returning Map<String, Crafting>**
> It is a method that return crafting Map.

## CraftingUtils

**CraftingUtils#removeRecipe
> It is a method that remove a recipe from server without restart!

## CraftingException
> This is an exception class that you can use!

## See (Important dependencies used)
- [panda-lang/expressible](https://github.com/panda-lang/expressible)
