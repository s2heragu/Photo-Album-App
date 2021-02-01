## Photo Album Application (JavaFX)

### Description
Java/JavaFX-based application for storing photo albums. Application takes file-paths of existing photos on user's local disk and additionally stores last-modified date, as well as photo tags.

### Functionality
#### Logging in as User
Each user can log in with his/her unique username. Program has an admin user (username = "admin") who can add new users and delete existing one. Upon initial use of the app, a stock user is created with sample photos.
#### User Functionality
##### Albums
User can add and delete albums as he/she pleases. User can also search for photos by dates or by photo tags.
##### Tags
User can add tags to, or delete tags from an existing photo. Tags have a type, and a value, both of which are entered by the user.
##### Photos
User can add photos to an album by selecting from a filechooser. For each album or search result, the user can also conduct a slideshow of the images.

### Storage
User data is stored through serialization in the users.dat file.
