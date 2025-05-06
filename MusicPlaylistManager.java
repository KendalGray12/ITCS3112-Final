import java.util.*;
import java.util.function.Predicate;

public class MusicPlaylistManager {
    public static void main(String[] args) {
        new PlaylistController().start();
    }
}

record Song(String title, String artist, String album, String genre) {
    @Override
    public String toString() {
        return String.format("%s - %s (%s) [%s]", artist, title, album, genre);
    }
}

class Playlist {
    private final String name;
    private final List<Song> songs;
    
    public Playlist(String name) {
        this.name = name;
        this.songs = new ArrayList<>();
    }
    
    public void addSong(Song song) {
        songs.add(song);
    }
    
    public boolean removeSong(String title) {
        return songs.removeIf(s -> s.title().equalsIgnoreCase(title));
    }
    
    public List<Song> search(Predicate<Song> criteria) {
        return songs.stream()
                   .filter(criteria)
                   .toList();
    }
    
    public void shuffle() {
        Collections.shuffle(songs);
    }
    
    public String getName() {
        return name;
    }
    
    public List<Song> getSongs() {
        return Collections.unmodifiableList(songs);
    }
    
    @Override
    public String toString() {
        return name + " (" + songs.size() + " songs)";
    }
}

class PlaylistController {
    private final Map<String, Playlist> playlists = new HashMap<>();
    private final Scanner scanner = new Scanner(System.in);
    private final List<String> popularArtists = List.of(
        "Kendrick Lamar", "Drake", "Beyoncé", "J. Cole",
        "Travis Scott", "Nicki Minaj", "Lil Wayne", "Jay-Z"
    );
    
    public void start() {
        System.out.println("🎵 Rap Music Playlist Manager 🎵");
        
        while (true) {
            printMenu();
            int choice = getIntInput("Choose an option: ");
            
            switch (choice) {
                case 1 -> createPlaylist();
                case 2 -> addSong();
                case 3 -> searchSongs();
                case 4 -> removeSong();
                case 5 -> viewPlaylists();
                case 6 -> shufflePlaylist();
                case 7 -> {
                    System.out.println("Goodbye!");
                    return;
                }
                default -> System.out.println("Invalid choice");
            }
        }
    }
    
    private void printMenu() {
        System.out.println("\nMain Menu:");
        System.out.println("1. Create playlist");
        System.out.println("2. Add song");
        System.out.println("3. Search songs");
        System.out.println("4. Remove song");
        System.out.println("5. View playlists");
        System.out.println("6. Shuffle playlist");
        System.out.println("7. Exit");
    }
    
    private void createPlaylist() {
        String name = getStringInput("Enter playlist name: ");
        if (playlists.containsKey(name)) {
            System.out.println("Playlist already exists!");
            return;
        }
        playlists.put(name, new Playlist(name));
        System.out.println("Playlist created: " + name);
    }
    
    private void addSong() {
        if (playlists.isEmpty()) {
            System.out.println("No playlists exist yet!");
            return;
        }
        
        String playlistName = getStringInput("Enter playlist name: ");
        Playlist playlist = playlists.get(playlistName);
        
        if (playlist == null) {
            System.out.println("Playlist not found!");
            return;
        }
        
        System.out.println("\nPopular Artists:");
        popularArtists.forEach(artist -> System.out.println("- " + artist));
        
        String artist = getStringInput("\nArtist: ");
        String title = getStringInput("Title: ");
        String album = getStringInput("Album: ");
        String genre = getStringInput("Genre: ");
        
        playlist.addSong(new Song(title, artist, album, genre));
        System.out.println("Song added!");
    }
    
    private void searchSongs() {
        System.out.println("\nSearch by:");
        System.out.println("1. Title");
        System.out.println("2. Artist");
        System.out.println("3. Genre");
        
        int choice = getIntInput("Your choice: ");
        String term = getStringInput("Search term: ").toLowerCase();
        
        Predicate<Song> criteria = switch (choice) {
            case 1 -> song -> song.title().toLowerCase().contains(term);
            case 2 -> song -> song.artist().toLowerCase().contains(term);
            case 3 -> song -> song.genre().toLowerCase().contains(term);
            default -> song -> false;
        };
        
        playlists.values().forEach(playlist -> {
            List<Song> results = playlist.search(criteria);
            if (!results.isEmpty()) {
                System.out.println("\nFound in " + playlist.getName() + ":");
                results.forEach(System.out::println);
            }
        });
    }
    
    private void removeSong() {
        String playlistName = getStringInput("Enter playlist name: ");
        Playlist playlist = playlists.get(playlistName);
        
        if (playlist == null) {
            System.out.println("Playlist not found!");
            return;
        }
        
        String title = getStringInput("Enter song title to remove: ");
        if (playlist.removeSong(title)) {
            System.out.println("Song removed successfully");
        } else {
            System.out.println("Song not found in playlist");
        }
    }
    
    private void viewPlaylists() {
        if (playlists.isEmpty()) {
            System.out.println("No playlists yet!");
            return;
        }
        
        System.out.println("\nYour Playlists:");
        playlists.values().forEach(playlist -> {
            System.out.println(playlist);
            playlist.getSongs().forEach(song -> 
                System.out.println("  " + song));
        });
    }
    
    private void shufflePlaylist() {
        String playlistName = getStringInput("Enter playlist name to shuffle: ");
        Playlist playlist = playlists.get(playlistName);
        
        if (playlist == null) {
            System.out.println("Playlist not found!");
            return;
        }
        
        playlist.shuffle();
        System.out.println("Playlist shuffled!");
    }
    
    private String getStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }
    
    private int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number");
            }
        }
    }
}