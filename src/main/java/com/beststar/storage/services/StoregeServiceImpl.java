package com.beststar.storage.services;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.beststar.storage.Constants;
import com.beststar.storage.entity.FileInfoResponse;
import com.beststar.storage.exceptions.StorageException;
import com.beststar.storage.properties.StorageProperties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class StoregeServiceImpl implements StorageService {

    private final Path rootLocation;
    
    @Autowired
    public StoregeServiceImpl(StorageProperties storageProperties) {
        this.rootLocation = Paths.get(storageProperties.getStorageLocation());
    }

    @Override
    public String storeFile(MultipartFile file) {
        createDir();
		Path storeFilePath = null;
		if (file.isEmpty()) {
			throw new StorageException("Failed to store empty file");
		}
		
		storeFilePath = getStoreFilePath(file.getOriginalFilename());
		if (!storeFilePath.getParent().equals(this.rootLocation.toAbsolutePath())) {
			throw new StorageException("Cannot store file outside current directory");
		}
		try {
			if (!checkFileContentType(storeFilePath)) {
				throw new StorageException("Unsaported MIME type of a file!");
			}
			try (InputStream inputStream = file.getInputStream()) {
				Files.copy(inputStream, storeFilePath, StandardCopyOption.REPLACE_EXISTING);
			}
		}
		catch (IOException e) {
			throw new StorageException("Failed to store file", e);
		}
		return storeFilePath.toString();
    }

	@Override
	public FileInfoResponse fileInfoRandomLine(String storeFilePath) {
		Path filePath = Paths.get(storeFilePath);
		String randomLine = "";
		long lineNumber = 0;
		long countLines = 0;
		String fileName = filePath.getFileName().toString();
		try (Stream<String> lines = Files.lines(filePath)) {
			countLines = Files.lines(filePath).count();
			lineNumber = getRandomRangeNumber(1, countLines);
			randomLine = lines.skip(lineNumber - 1).findFirst().get();
		} catch(IOException e) {
			throw new StorageException(e.getMessage());
		}
		if (Objects.nonNull(randomLine) && !randomLine.isEmpty()) {
			char mostOftenOccurLetter = getMostOftenOccurLetter(randomLine);
			String backwardsLine = new StringBuilder(randomLine).reverse().toString();
			return new FileInfoResponse(lineNumber, fileName, randomLine, backwardsLine, mostOftenOccurLetter);
		} else {
			return new FileInfoResponse(lineNumber, fileName);
		}
	}

	@Override
	@Async("threadPoolTaskExecutor")
	public CompletableFuture<FileInfoResponse> fileInfoRandomLineAsync(String storeFilePath) throws InterruptedException {
		Path filePath = Paths.get(storeFilePath);
		String randomLine = "";
		long lineNumber = 0;
		long countLines = 0;
		String fileName = filePath.getFileName().toString();
		try (FileInputStream fileInputStream = new FileInputStream(fileName);
			 FileChannel channel = fileInputStream.getChannel();
			 FileLock lock = channel.lock(0, Long.MAX_VALUE, true)) {
			if (Objects.nonNull(lock)) {	 
				countLines = Files.lines(filePath).count();
				lineNumber = getRandomRangeNumber(1, countLines);
				randomLine = Files.lines(filePath).skip(lineNumber - 1).findFirst().get();
			}	
		} catch(OverlappingFileLockException | IOException e) {
			throw new StorageException(e.getMessage());
		}
		if (Objects.nonNull(randomLine) && !randomLine.isEmpty()) {
			char mostOftenOccurLetter = getMostOftenOccurLetter(randomLine);
			String backwardsLine = new StringBuilder(randomLine).reverse().toString();
			return CompletableFuture.completedFuture(new FileInfoResponse(lineNumber, fileName, randomLine, backwardsLine, mostOftenOccurLetter));
		} else {
			return CompletableFuture.completedFuture(new FileInfoResponse(lineNumber, fileName));
		}
	}
	
	@Override
	public boolean fileExists(String fileName) {
		return Files.exists(getStoreFilePath(fileName));
	}

	@Override
	public Path getStoreFilePath(String fileName) {
		return this.rootLocation.resolve(Paths.get(fileName)).normalize().toAbsolutePath();
	}

	private char getMostOftenOccurLetter(String line) {
		return line.chars()
			.filter(c -> Character.isWhitespace(c) == false)
			.mapToObj(c -> (char) c)
			.collect(Collectors.groupingBy(c -> c, Collectors.counting()))
			.entrySet()
			.stream()
			.max(Map.Entry.comparingByValue())
			.map(p -> p.getKey())
			.get();
	}

	private boolean checkFileContentType(Path path) throws IOException {
		String mimeType = Files.probeContentType(path);
		return Constants.permissibleAccept.contains(mimeType);	
	}

	private long getRandomRangeNumber(long min, long max) {
		return new Random().nextLong(max - min) + min;
	}

	private void createDir() {
		try {
			Files.createDirectories(rootLocation);
		}
		catch (IOException e) {
			throw new StorageException("Could not initialize storage", e);
		}
	}
}
