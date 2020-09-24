# Code Similarity Disguiser

**Code** **similarity** **disguiser** \(CSD\) is a tool to educate students about code similarity for academic integrity in programming. With the tool, learners can independently learn about the many ways that a program can be changed without affecting the underlying code similarity. It can also be used to individualise code-tracing assessments, to anonymise identifying information in student programs, and to generate data sets for the evaluation of code similarity detection that incorporates features of programming style. Further details can be seen in the corresponding paper.  


**CSD** provides four modes:  
1. **disguise**: given a source code project and a list of desired disguises, this mode will disguise the project's code based on the desired disguises. Please be aware that the
  impact of some disguises might overlap one another. A complete list of disguise IDs can be seen via the fourth mode.  
  *Command*: 
  ```
  disguise <input_project_path> <programming_language> <human_language> <additional_keywords_path> <disguise_id_1> <disguise_id_2> ... <disguise_id_N>  
  ```  
  
2. **diagnose**: given a source code project, this mode will list any applicable disguises.  
  *Command*: 
  ```
  diagnose <input_project_path> <programming_language> <human_language> <additional_keywords_path> <disguise_id_1> <disguise_id_2> ... <disguise_id_N>  
  ```  
  
3. **disguiserandom**: this mode is similar to the first one except that the disguises are randomly applied. The code project will be diagnosed first to assure the applicability
  of the disguises.  
  *Command*: 
  ```
  disguiserandom <input_project_path> <programming_language> <human_language> <additional_keywords_path> <num_comment_disguises> <num_whitespace_disguises> <num_ident_disguises> <num_constant_and_data_type_disguises>  
  ```  
  
4. **availabledisguises**: this mode will list all available disguises.  
  *Command*: 
  ```
  availabledisguises  
  ```  


**Parameters** **description** **in** **alphabetical** **order**:  
  1. *<additional_keywords_path>*: a string representing a file containing additional keywords with newline as the delimiter. Keywords with more than one token should be written by embedding spaces between the tokens. For example, 'System.out.print' should be written as \'System . out . print\'. If unused, please set this to \'null\'.  
  
  2. *<disguise_id>*: a number representing a code disguise. See the complete list at the end of this help.  
      values: 0-59.  
      
  3. *<human_language>*: a constant depicting the human language used on the applied disguises.  
    values: 'en' for English or 'id' for Indonesian.  
    
  4. *<input_project_path>*: a string representing the path of the source code projects for input. Please use quotes if the path contains spaces.  
  
  5. *<num_comment_disguises>*: a number depicting the maximum number of comment disguises that will be applied.  
    values: a non-negative integer lower or equal to 27.  
    
  6. *<num_constant_and_data_type_disguises>*: a number depicting the maximum number of constant disguises that will be applied.  
    values: a non-negative integer lower or equal to 15.  
    
  7. *<num_ident_disguises>*: a number depicting the maximum number of identifier disguises that will be applied.  
    values: a non-negative integer lower or equal to 10.  
    
  8. *<num_whitespace_disguises>*: a number depicting the maximum number of whitespace disguises that will be applied.  
    values: a non-negative integer lower or equal to 8.  
    
  9. *<programming_language>*: a constant depicting the programming language used on given source code files.  
    values: 'java' (for Java) or 'py' (for Python).  
