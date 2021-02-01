## Photo Album Application (Android)

### Description
Android based application for storing photo albums. Application takes file-paths of existing photos on phone's emulated storage and additionally allows for creation and storage of photo tags.

### Functionality
The app has only one user - the phone's owner. Upon opening the app, the user can see a list of albums, along with a module for searching photos.
#### User Functionality
##### Albums
User can add, rename and delete albums as he/she pleases. User can also search for photos by dates or by photo tags.
##### Tags
User can add tags to, or delete tags from an existing photo. Tags have a type (location or person), and a value (entered by the user). 
##### Photos
User can add photos to an album by selecting from a filechooser - this opens up the phone's gallery. For each album or search result, the user can also conduct a slideshow of the images.

### Storage
User data is stored through serialization in the users.dat file.
