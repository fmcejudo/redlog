package io.github.fmcejudo.redlogs.card.converter;

import io.github.fmcejudo.redlogs.card.CardMetadata;
import io.github.fmcejudo.redlogs.card.CardQuery;
import io.github.fmcejudo.redlogs.card.CardQueryRequest;

public interface CardQueryConverter {

  CardQueryRequest convert(CardQuery cardQuery, CardMetadata cardMetadata);

}
