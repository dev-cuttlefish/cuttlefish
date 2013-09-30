
    CUTTLEFISH DOCUMENTATION
    
    This document contains the revision of Cuttlefish Documentation, as it 
    exists on the MediaWiki of the project. Due to inability of access to the 
    MediaWiki, this file intends to provide guidelines, information and new 
    data for the content of MediaWiki.
    
    MediaWiki
    https://sourceforge.net/apps/mediawiki/cuttlefish/index.php?title=Main_Page


    Ilias Rinis (iliasr -at- gmail.com)
    30 Sept. 2013



    I. Cuttlefish technology statement

While Cuttlefish 2.0 heavily built on the JUNG library, the latest version of
Cuttlefish now replaces JUNG with two different libraries:

1. Gephi Toolkit v0.8.5 handles the Graph Library (Data Structures, Access and 
Control, Layout algorithms, Input/Output controllers)

2. JOGL v2.0.2 (The Java bindings for OpenGL) is used for the visualization of
the graphs. A custom visualization engine
(module ch.ethz.sg.cuttlefish.gui.visualization) was built on top of JOGL. 
This engine handles element rendering (vertices, edges, labels), as well as 
interaction with the system (layouts, controls) and the user (mouse, zooming,
panning, etc.).


