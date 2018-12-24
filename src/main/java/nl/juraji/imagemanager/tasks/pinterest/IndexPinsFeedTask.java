package nl.juraji.imagemanager.tasks.pinterest;

import nl.juraji.imagemanager.model.domain.pinterest.PinMetaData;
import nl.juraji.imagemanager.model.domain.pinterest.PinterestBoard;
import nl.juraji.imagemanager.model.finders.PinterestBoardsFinder;
import nl.juraji.imagemanager.model.web.pinterest.resources.ResourceResult;
import nl.juraji.imagemanager.model.web.pinterest.types.PinResource;
import nl.juraji.imagemanager.model.web.pinterest.types.PinResourceImage;
import nl.juraji.imagemanager.util.StringUtils;
import nl.juraji.imagemanager.util.URIUtils;
import nl.juraji.imagemanager.util.types.AtomicString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Created by Juraji on 4-12-2018.
 * Image Manager 2
 */
public abstract class IndexPinsFeedTask extends PinterestWebTask<Void> {
    private static final int PIN_FETCH_COUNT_OFFSET = 100;

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final PinterestBoard board;
    private final boolean doReindex;

    public IndexPinsFeedTask(PinterestBoard board, boolean doReindex) {
        super("Indexing Pinterest %s: %s", board.getType().toString(), board.getName());
        this.board = board;
        this.doReindex = doReindex;
    }

    @Override
    protected Void call() throws Exception {
        super.init();

        final Set<PinMetaData> existingMetaData = board.getMetaData();
        final int knownMetaDataSize = existingMetaData.size();
        final int reportedPinCount = getReportedPinCount();
        final int pinsToFetchCount;

        if (doReindex) {
            pinsToFetchCount = reportedPinCount + PIN_FETCH_COUNT_OFFSET;
            logger.info("Reindexing entire {}!", board.getType().toString());
        } else {
            pinsToFetchCount = reportedPinCount - knownMetaDataSize + PIN_FETCH_COUNT_OFFSET;
        }

        logger.info("{}: Known pins: {}, Reported pin count: {}", board.getName(), knownMetaDataSize, reportedPinCount);

        if (reportedPinCount <= knownMetaDataSize) {
            logger.info("No index needed for {}", board.getName());
            return null;
        }

        setTotalWork(pinsToFetchCount);
        final List<PinMetaData> fetchedPins = new ArrayList<>();
        final AtomicString bookmarkTemp = new AtomicString();

        do {
            this.checkCanceled();
            final ResourceResult<List<PinResource>> result = this.getPinsFeed(board.getBoardId(), bookmarkTemp.get());

            bookmarkTemp.set(result.getBookmark());

            result.getData().stream()
                    .map(pinResource -> this.mapPinResourceToPinMetaData(pinResource, existingMetaData))
                    .filter(Objects::nonNull)
                    .forEach(fetchedPins::add);

            setProgress(fetchedPins.size());
        } while (fetchedPins.size() < pinsToFetchCount && !bookmarkTemp.equals("-end-"));

        logger.info("Fetched {} new pins for {}", fetchedPins.size(), board.getName());
        PinterestBoardsFinder.find().db().saveAll(fetchedPins);
        board.getMetaData().addAll(fetchedPins);

        return null;
    }

    protected PinterestBoard getBoard() {
        return board;
    }

    private PinMetaData mapPinResourceToPinMetaData(PinResource pinResource, Set<PinMetaData> existingMetaData) {
        if ("pin".equals(pinResource.getType())) {

            // Find existing pin
            final PinMetaData pin = existingMetaData.stream()
                    .filter(ep -> ep.getPinId().equals(pinResource.getId()))
                    .findFirst()
                    .orElse(new PinMetaData());


            if (pin.getPinId() == null) {
                pin.setPinId(pinResource.getId());
                pin.setBoard(board);
            }

            if (!URIUtils.contains(pin.getPinterestUri(), board.getName())) {
                pin.setPinterestUri(board.getBoardUrl().resolve("/pin/" + pinResource.getId()));
            }

            String sourceUrl = pinResource.getLink();
            if (StringUtils.isNotEmpty(sourceUrl)) {
                pin.setSourceUrl(URI.create(sourceUrl));
            }

            final PinResourceImage origImage = pinResource.getImages().getOriginal();
            pin.setDownloadUrl(URI.create(origImage.getUrl()));
            pin.setWidth(origImage.getWidth());
            pin.setHeight(origImage.getHeight());

            pin.setTitle(pinResource.getTitle());
            pin.setComments(pinResource.getDescription().trim());

            return pin;
        }

        return null;
    }

    protected abstract ResourceResult<List<PinResource>> getPinsFeed(String boardId, String bookmark) throws Exception;

    protected abstract int getReportedPinCount() throws Exception;
}
