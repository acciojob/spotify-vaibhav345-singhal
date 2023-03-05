package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class SpotifyRepository {
    public HashMap<Artist, List<Album>> artistAlbumMap;
    public HashMap<Album, List<Song>> albumSongMap;
    public HashMap<Playlist, List<Song>> playlistSongMap;
    public HashMap<Playlist, List<User>> playlistListenerMap;
    public HashMap<User, Playlist> creatorPlaylistMap;
    public HashMap<User, List<Playlist>> userPlaylistMap;
    public HashMap<Song, List<User>> songLikeMap;

    public List<User> users;
    public List<Song> songs;
    public List<Playlist> playlists;
    public List<Album> albums;
    public List<Artist> artists;

    public SpotifyRepository() {
        //To avoid hitting apis multiple times, initialize all the hashmaps here with some dummy data
        artistAlbumMap = new HashMap<>();
        albumSongMap = new HashMap<>();
        playlistSongMap = new HashMap<>();
        playlistListenerMap = new HashMap<>();
        creatorPlaylistMap = new HashMap<>();
        userPlaylistMap = new HashMap<>();
        songLikeMap = new HashMap<>();

        users = new ArrayList<>();
        songs = new ArrayList<>();
        playlists = new ArrayList<>();
        albums = new ArrayList<>();
        artists = new ArrayList<>();
    }

    public User createUser(String name, String mobile) {

        for (User user : users) {
            if (user.getMobile().equals(mobile)) {
                return user;
            }
        }
        User user1 = new User(name, mobile);
        users.add(user1);
        userPlaylistMap.put(user1, new ArrayList<>());

        return user1;
    }

    public Artist createArtist(String name) {

        for (Artist artist : artists) {
            if (artist.getName().equals(name)) {
                return artist;
            }
        }

        Artist artist1 = new Artist(name);
        artists.add(artist1);
        artistAlbumMap.put(artist1, new ArrayList<>());

        return artist1;
    }

    public Album createAlbum(String title, String artistName) {
        List<Album> temp;
        Artist artist = createArtist(artistName);
        for (Album album : albums) {
            if (album.getTitle().equals(title)) {
                return album;
            }
        }

        Album album = new Album(title);
        albums.add(album);

        if (artistAlbumMap.containsKey(artist) && artistAlbumMap.get(artist) != null) {
            temp = artistAlbumMap.get(artist);
        } else {
            temp = new ArrayList<>();
        }
        temp.add(album);
        artistAlbumMap.put(artist, temp);
        return album;
    }

    public Song createSong(String title, String albumName, int length) throws Exception {
        Album album = new Album();
        boolean albumExist = false;
        for (Album album1 : albums) {
            if (album1.getTitle().equals(albumName)) {
                albumExist = true;
                album = album1;
                break;
            }
        }
        if (!albumExist) {
            throw new Exception("Album does not exist");
        }

        Song song = new Song(title, length);
        songs.add(song);

        List<Song> songList;
        if (albumSongMap.containsKey(album)) {
            songList = albumSongMap.get(album);
        } else {
            songList = new ArrayList<>();
        }

        songList.add(song);
        albumSongMap.put(album, songList);
        return song;
    }

    public Playlist createPlaylistOnLength(String mobile, String title, int length) throws Exception {

        for (Playlist playlist : playlists) {
            if (playlist.getTitle().equals(title)) {
                return playlist;
            }
        }
        Playlist playlist = new Playlist(title);
        playlists.add(playlist);

        List<Song> temp = new ArrayList<>();
        for (Song song : songs) {
            if (length == song.getLength()) {
                temp.add(song);
                playlistSongMap.put(playlist, temp);
            }
        }

        boolean userExist = false;
        for (User user : users) {
            if (user.getMobile().equals(mobile)) {
                userExist = true;
                List<User> userList = new ArrayList<>();
                if (playlistListenerMap.containsKey(playlist)) {
                    userList = playlistListenerMap.get(playlist);
                }

                userList.add(user);
                playlistListenerMap.put(playlist, userList);
                creatorPlaylistMap.put(user, playlist);

                List<Playlist> playlistList = new ArrayList<>();
                if (userPlaylistMap.containsKey(user)) {
                    playlistList = userPlaylistMap.get(user);
                }
                playlistList.add(playlist);
                userPlaylistMap.put(user, playlistList);
                break;
            }
        }

        if (!userExist) {
            throw new Exception("User does not exist");
        }

        return playlist;
    }

    public Playlist createPlaylistOnName(String mobile, String title, List<String> songTitles) throws Exception {
        for (Playlist playlist : playlists) {
            if (playlist.getTitle().equals(title)) {
                return playlist;
            }
        }
        Playlist playlist = new Playlist(title);
        playlists.add(playlist);

        List<Song> temp = new ArrayList<>();
        for (Song song : songs) {
            if (songTitles.contains(song.getTitle())) {
                temp.add(song);
            }
        }
        playlistSongMap.put(playlist, temp);

        boolean userExist = false;
        for (User user : users) {
            if (user.getMobile().equals(mobile)) {
                userExist = true;
                List<User> userList = new ArrayList<>();
                if (playlistListenerMap.containsKey(playlist)) {
                    userList = playlistListenerMap.get(playlist);
                }

                userList.add(user);
                playlistListenerMap.put(playlist, userList);
                creatorPlaylistMap.put(user, playlist);

                List<Playlist> playlistList = new ArrayList<>();
                if (userPlaylistMap.containsKey(user)) {
                    playlistList = userPlaylistMap.get(user);
                }
                playlistList.add(playlist);
                userPlaylistMap.put(user, playlistList);
                break;
            }
        }

        if (!userExist) {
            throw new Exception("User does not exist");
        }

        return playlist;
    }

    public Playlist findPlaylist(String mobile, String playlistTitle) throws Exception {
        User user1 = new User();
        Playlist playlist1 = new Playlist();
        boolean userExist = false;
        boolean playListExist = false;
        for (User user : users) {
            if (user.getMobile().equals(mobile)) {
                userExist = true;
                user1 = user;
                break;
            }
        }

        if (!userExist) {
            throw new Exception("User does not exist");
        }

        for (Playlist playlist : playlists) {
            if (playlist.getTitle().equals(playlistTitle)) {
                playListExist = true;
                playlist1 = playlist;
                break;
            }
        }
        if (!playListExist) {
            throw new Exception("Playlist does not exist");
        }

        List<User> temp = new ArrayList<>();
        if (playlistListenerMap.containsKey(playlist1)) {
            temp = playlistListenerMap.get(playlist1);
        }
        if (!temp.contains(user1)) {
            temp.add(user1);
        }
        playlistListenerMap.put(playlist1, temp);

        if (creatorPlaylistMap.get(user1) != playlist1)
            creatorPlaylistMap.put(user1, playlist1);

        List<Playlist> userplaylists = new ArrayList<>();
        if (userPlaylistMap.containsKey(user1)) {
            userplaylists = userPlaylistMap.get(user1);
        }
        if (!userplaylists.contains(playlist1)) userplaylists.add(playlist1);
        userPlaylistMap.put(user1, userplaylists);


        return playlist1;
    }

    public Song likeSong(String mobile, String songTitle) throws Exception {
        User user1 = null;
        Song song1 = null;
        boolean userExist = false;
        boolean songExist = false;
        for (User user : users) {
            if (user.getMobile().equals(mobile)) {
                userExist = true;
                user1 = user;
            }
        }

        if (!userExist) {
            throw new Exception("User does not exist");
        }

        for (Song song : songs) {
            if (song.getTitle().equals(songTitle)) {
                songExist = true;
                song1 = song;
            }
        }
        if (!songExist) {
            throw new Exception("Song does not exist");
        }

        List<User> temp = new ArrayList<>();
        if (songLikeMap.containsKey(song1)) {
            temp = songLikeMap.get(song1);
        }

        if (!temp.contains(user1)) {
            temp.add(user1);
            int currLikes = song1.getLikes();
            song1.setLikes(song1.getLikes() + 1);
            songLikeMap.put(song1, temp);
        }

        Album album = null;
        for (Map.Entry<Album, List<Song>> albumListEntry : albumSongMap.entrySet()) {
            for (Song song : albumListEntry.getValue()) {
                if (song.getTitle().equals(songTitle)) {
                    album = albumListEntry.getKey();
                    break;
                }
            }
        }

        Artist artist = new Artist();
        for (Artist artist1 : artistAlbumMap.keySet()) {
            List<Album> albumList = artistAlbumMap.get(artist1);
            if (albumList.contains(album)) {
                artist = artist1;
                break;
            }
        }
        artist.setLikes(artist.getLikes() + 1);
        return song1;
    }

    public String mostPopularArtist() {
        int max = Integer.MIN_VALUE;
        String artist = null;

        for (Artist artist1 : artists) {
            if (artist1.getLikes() > max) {
                max = artist1.getLikes();
                artist = artist1.getName();
            }
        }
        return artist;
    }

    public String mostPopularSong() {
        int max = Integer.MIN_VALUE;
        String song = null;

        for (Song song1 : songs) {
            if (song1.getLikes() > max) {
                max = song1.getLikes();
                song = song1.getTitle();
            }
        }
        return song;
    }
}
