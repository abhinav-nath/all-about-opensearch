# Mapping and Analysis

## Analysis

![Analyzer](./images/analyzer.png "Analyzer")

### Character filters

* Adds, removes or changes characters
* Analyzers contain zero or more character filters
* Character filters are applied in the order in which they are specified
* Example (`html strip` filter)
  - **Input: "**I&apos;m in a <em>good</em> mood&nbsp;-&nbsp;and
    I <strong>love</strong> coffee!**"**
  - **Output: "**I'm in a good mood - and I love coffee!**"**

### Tokenizers

* An analyzer contains **one** tokenizer
* Tokenizes a string, i.e. splits it into tokens
* Characters may be stripped part of tokenization
* Example
   - **Input: "**I REALLY like beer!**"**
   - **Output:** ["I", "REALLY", "like", "beer"]
* The tokenizer also records the character offsets for each token in the original string

### Token filters

* Receive the output of the tokenizer as input (i.e. the tokens)
* A token filter can add, remove, or modify tokens
* An analyzer contains zero or more token filters
* Token filters are applied in the order in which they are specified
* Example (`lowercase` filter)
   - **Input: "**I REALLY like beer!**"**
   - **Output:** ["i", "really", "like", "beer"]

### Built-in and custom components

* Built-in analyzers, character filters, tokenizers, and token filters are available
* We can also build custom ones

### Default working

#### Analyzer - standard
* Standard analyzer is the default analyzer of ES
* It is used for all `text` fields unless configured otherwise

#### Character Filters - none

**Input: "**I REALLY like beer!**"**</br>
**Output: "**I REALLY like beer!**"**

* No character filter is used by default
* So the text is passed to the tokenizer as it is

#### Tokenizer - standard

**Input: "**I REALLY like beer!**"**</br>
**Output:** ["I", "REALLY", "like", "beer"]

* The tokenizer splits the text into tokens according to the unicode segmentation algorithm
* Essentially it breaks sentences by whitespaces, hyphens etc
* In the process, it also throws away punctuations such as commas, periods, exclamation marks etc

#### Token filters - lowercase

**Input:** ["I", "REALLY", "like", "beer"]</br>
**Output:** ["i", "really", "like", "beer"]

* The tokenizer splits the text into tokens according to the unicode segmentation algorithm
* Essentially it breaks sentences by whitespaces, hyphens etc
* In the process, it also throws away punctuations such as commas, periods, exclamation marks etc
