/**
 * Convert a search query retrieved from the user into a regular expression
 * that can be used to search page content.
 *
 * @param {string} query The search query
 * @returns {RegExp} A new regular expression that will match the search query
 */
function getSearchRegex(query) {
  const escapedQuery = query.replace(/[-/\\^$*+?.()|[\]{}]/g, '\\$&');
  return new RegExp(escapedQuery, 'i');
}

/**
 * Search a page against a given regular expression.
 *
 * @param {Object} page The page to search
 * @param {RegExp} regex The regular expression to match
 * @returns {number} A weighting indicating to what extent the page matches
 *                   the search
 */
function searchPage(page, regex) {
  if (page.content) {
    let weight = searchNode(JSON.parse(page.content), regex);
    if (regex.test(page.title)) weight += 7;
    return weight;
  }
  else return 0;
}

/**
 * Search a content node recursively against a given regular expression.
 *
 * @param {Object} node The node to search
 * @param {RegExp} regex The regular expression to match
 * @returns {number} A weighting indicating to what extent the node (and its
 *                   children) matches the search
 */
function searchNode(node, regex) {
  switch(node.type) {
    case "paragraph": { // Base case
      const text = getCombinedContent(node);
      return regex.test(text) ? 1 : 0;
    }
    case "heading": { // Base case
      const text = getCombinedContent(node);
      return regex.test(text) ? (6 - node.attrs.level) : 0;
    }
    default: { // Recursive case
      if (!node.content) return 0;
      return node.content
                 .map(child => searchNode(child, regex))
                 .reduce((a, b) => a + b, 0);
    }
  }
}

/**
 * Combine the text content of the immediate children of a node into a single string.
 *
 * @param {Object} node The parent node
 * @returns {string} The combined text content, or the empty string if there
 *                   is no content
 */
function getCombinedContent(node) {
  if (!node.content) return '';
  let result = "";
  node.content.forEach(contentNode => {
    if (contentNode.type === "text") result += contentNode.text;
  });
  return result;
}

export default {
  /**
   * Search an array of pages against a given query.
   *
   * @param {Object[]} pages The pages to search
   * @param {string} query The search query
   * @returns {Object[]} All pages that contain the search query, ordered from
   *                     best to worst match
   */
  search(pages, query) {
    const regex = getSearchRegex(query);
    return Array.from(pages, page => ({
                  page: page,
                  weight: searchPage(page, regex)
                }))
                .filter(result => result.weight > 0)
                .sort((a, b) => b.weight - a.weight)
                .map(result => result.page);
  }
}
