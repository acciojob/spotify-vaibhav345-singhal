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
        User user = new User(name, mobile);
        if (!users.contains(user)) {
            users.add(user);
            userPlaylistMap.put(user, new ArrayList<>());
        }
        return user;
    }

    public Artist createArtist(String name) {
        Artist artist = new Artist(name);
        if (!artists.contains(artist)) {
            artists.add(artist);
            artistAlbumMap.put(artist, new ArrayList<>());
        }
        return artist;
    }

    public Album createAlbum(String title, String artistName) {
        List<Album> temp;
        Artist artist = new Artist(artistName);
        Album album;

        boolean artistExist = false;

        for (Artist artist1 : artists) {
            if (artist.equals(artist1)) {
                artistExist = true;
                break;
            }
        }
        if (!artistExist) {
            artists.add(artist);
            artistAlbumMap.put(artist, new ArrayList<>());
        }

        temp = artistAlbumMap.get(artist);

        album = new Album(title);
        albums.add(album);
        albumSongMap.put(album, new ArrayList<>());

        temp.add(album);
        artistAlbumMap.put(artist, temp);
        return album;
    }

    public Song createSong(String title, String albumName, int length) throws Exception {
        Album album = new Album(albumName);
        if (!albums.contains(album)) {
            throw new Exception("Album does not exist");
        }

        Song song = new Song(title, length);
        songs.add(song);

        List<Song> songList = albumSongMap.get(album);
        songList.add(song);
        songLikeMap.put(song, new ArrayList<>());
        return song;
    }

    public Playlist createPlaylistOnLength(String mobile, String title, int length) throws Exception {

        Playlist playlist = new Playlist(title);
        playlists.add(playlist);
        playlistSongMap.put(playlist, new ArrayList<>());

        for (Song song : songs) {
            if (length == song.getLength()) {
                List<Song> temp = playlistSongMap.get(playlist);
                temp.add(song);
                playlistSongMap.put(playlist, temp);
                playlistListenerMap.put(playlist, new ArrayList<>());
            }
        }

        boolean userExist = false;
        for (User user : users) {
            if (user.getMobile().equals(mobile)) {
                userExist = true;
                List<User> temp = playlistListenerMap.get(playlist);
                temp.add(user);
                playlistListenerMap.put(playlist, temp);
                creatorPlaylistMap.put(user, playlist);

                List<Playlist> playlistList = userPlaylistMap.get(user);
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
        Playlist playlist = new Playlist(title);
        playlists.add(playlist);
        playlistSongMap.put(playlist, new ArrayList<>());

        for (String name : songTitles) {
            for (Song song : songs) {
                if (name.equals(song.getTitle())) {
                    List<Song> temp = playlistSongMap.get(playlist);
                    temp.add(song);
                    playlistSongMap.put(playlist, temp);
                    playlistListenerMap.put(playlist, new ArrayList<>());
                }
            }
        }

        boolean userExist = false;
        for (User user : users) {
            if (user.getMobile().equals(mobile)) {
                userExist = true;
                List<User> temp = playlistListenerMap.get(playlist);
                temp.add(user);
                playlistListenerMap.put(playlist, temp);
                creatorPlaylistMap.put(user, playlist);

                List<Playlist> playlistList = userPlaylistMap.get(user);
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
        User user1 = null;
        Playlist playlist1 = null;
        boolean userExist = false;
        boolean playListExist = false;
        for (User user : users) {
            if (user.getMobile().equals(mobile)) {
                userExist = true;
                user1 = user;
            }
        }

        if (!userExist) {
            throw new Exception("User does not exist");
        }

        for (Playlist playlist : playlists) {
            if (playlist.getTitle().equals(playlistTitle)) {
                playListExist = true;
                playlist1 = playlist;
            }
        }
        if (!playListExist) {
            throw new Exception("Playlist does not exist");
        }

        List<User> temp = playlistListenerMap.get(playlist1);
        for (User user : temp) {
            if (user.getMobile().equals(mobile)) {
                return playlist1;
            }
        }

        temp.add(user1);
        playlistListenerMap.put(playlist1, temp);

        List<Playlist> playlistList = userPlaylistMap.get(user1);
        playlistList.add(playlist1);
        userPlaylistMap.put(user1, playlistList);
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

        List<User> temp = songLikeMap.get(song1);
        for (User user : temp) {
            if (user.getMobile().equals(mobile)) {
                return song1;
            }
        }

        int currLikes = song1.getLikes();
        song1.setLikes(currLikes + 1);
        temp.add(user1);

        songLikeMap.put(song1, temp);

        Album album = null;
        for (Map.Entry<Album, List<Song>> albumListEntry : albumSongMap.entrySet()) {
            for (Song song : albumListEntry.getValue()) {
                if (song.getTitle().equals(songTitle)) {
                    album = albumListEntry.getKey();
                }
            }
        }

        for (Map.Entry<Artist, List<Album>> artistListEntry : artistAlbumMap.entrySet()) {
            for (Album album1 : artistListEntry.getValue()) {
                if (album1 == album) {
                    int artistLikes = artistListEntry.getKey().getLikes();
                    artistListEntry.getKey().setLikes(artistLikes + 1);
                }
            }
        }
        return song1;
    }

    public String mostPopularArtist() {
        int max = 0;
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
        int max = 0;
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
