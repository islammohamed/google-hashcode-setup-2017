package org.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class App {
    public static void main(String[] args) {
        String inputPath = args[0];
        try {
            List<String> inputLines = Files.readAllLines(Paths.get(inputPath));

            String[] header = inputLines.get(0).split(" ");

            final int cacheSize = Integer.parseInt(header[4]);


            int[] videos = new int[Integer.parseInt(header[0])];
            int[] caches = new int[Integer.parseInt(header[3])];
            // First latency of endpoint is the one to the datacenter and the following of the caches -> meaning the actual cache id is the id in the array - 1
            List<Map<Integer, Integer>> endpoints = new ArrayList<>();

            int[][] requests = new int[Integer.parseInt(header[2])][3];

            String[] videoSizes = inputLines.get(1).split(" ");

            //init the videos
            int i = 0;
            for (String videoSize : videoSizes) {
                videos[i] = Integer.parseInt(videoSize);
                i++;
            }

            //@TODO ignore endpoints without cache
            //from the third line
            int endpointCounter = 0;
            int lineCounter = 2;
            while (true) {
                String[] endpointHeader = inputLines.get(lineCounter).split(" ");

                if (endpointHeader.length == 3) {
                    break;
                }

                int cacheNumber = Integer.parseInt(endpointHeader[1]);

                Map<Integer, Integer> cacheMap = new HashMap<>();
                endpoints.add(cacheMap);
                cacheMap.put(0, Integer.parseInt(endpointHeader[0]));
                lineCounter++;
                for (int cacheCounter = 1; cacheCounter <= cacheNumber; cacheCounter++) {
                    String[] cacheProps = inputLines.get(lineCounter).split(" ");
                    cacheMap.put(Integer.parseInt(cacheProps[0]) + 1, Integer.parseInt(cacheProps[1]));
                    lineCounter++;
                }
                //
// endpoints[endpointCounter][cacheCounter] = Integer.parseInt(endpointHeader[0]);
            }
            int requestCounter = 0;
            while (lineCounter < inputLines.size()) {
                String[] requestProps = inputLines.get(lineCounter).split(" ");

                requests[requestCounter] = new int[]{Integer.parseInt(requestProps[0]), Integer.parseInt(requestProps[1]), Integer.parseInt(requestProps[2])};

                requestCounter++;
                lineCounter++;
            }
            //start the algorithm implementation
            Map<Integer, ArrayList<Integer[]>> cacheToResult = new HashMap<>();

            for (int requestIndex = 0; requestIndex < requests.length; requestIndex++) {
                int[] request = requests[requestIndex];
                int requestQty = request[2];
                int videoId = request[0];
                int endpointId = request[1];
                int videoSize = videos[videoId];
                Map<Integer, Integer> cacheToLatency = endpoints.get(endpointId);

                for (Map.Entry<Integer, Integer> entry : cacheToLatency.entrySet()) {
                    int sourceId = entry.getKey();
                    int latency = entry.getValue();

                    if (cacheToResult.containsKey(sourceId)) {
                        ArrayList<Integer[]> prevResults = cacheToResult.get(sourceId);
                        int score = videoSize * requestQty / latency;
                        Integer[] result = new Integer[]{videoId, endpointId, videoSize, requestQty, latency, score};
                        prevResults.add(result);
                    } else {
                        cacheToResult.put(sourceId, new ArrayList<>());
                    }
                }

            }

//            for (int endpointIndex = 0; endpointIndex < endpoints.size(); endpointIndex++) {
//                for (int requestIndex = 0; requestIndex < requests.length;requestIndex++) {
//                    endpoints.get(endpointIndex)
//                }
//            }


//            for () {
//
//            }

            for (Map.Entry<Integer, ArrayList<Integer[]>> entry : cacheToResult.entrySet()) {
                entry.getValue().sort((s1, s2) -> {
                    if (s1[5] > s2[5]) {
                        return -1;
                    } else if (s1[5] < s2[5]) {
                        return 1;
                    }
                    return 0;
                });
            }

            System.out.println(caches.length);

            for (Map.Entry<Integer, ArrayList<Integer[]>> entry : cacheToResult.entrySet()) {
                if (entry.getKey() == 0) {
                    continue;
                }

                int currentCacheSize = 0;
                Set<Integer> uniqueVideos = new HashSet<>();
                System.out.print(entry.getKey() - 1 + " ");
                for (Integer[] result : entry.getValue()) {
                    if (!uniqueVideos.contains(result[0]) && currentCacheSize + result[2] < cacheSize) {
                        System.out.print(result[0] + " ");
                        uniqueVideos.add(result[0]);
                        currentCacheSize += result[2];
                    }
                }
                System.out.print("\n");
            }
        } catch (IOException e) {
            System.err.println("Could not read input file: " + e.getMessage());
            e.printStackTrace();
        }

    }
}
