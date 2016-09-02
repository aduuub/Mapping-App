This is a mapping application for the Auckland reigon. Data was retrieved and modified from google maps for the locational data. 
It can:
- Manually navigate the map
- Search for streets with predictions by storing the roads in a trie-structure.
- Find articulation points (intersections that if blocked would prevent traffic flow to specific streets).
- Directions from point to point. The search for best path is performed by A* search and takes into account one way streets.
