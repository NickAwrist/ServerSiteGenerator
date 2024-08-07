function showMessage() {
    alert('Hello from your Minecraft server!');
}

document.getElementById('discordButton').addEventListener('click', function() {
    alert('Insert Discord invite link here');
});

function toggleFAQ(element) {
    const content = element.nextElementSibling;
    if (content.style.display === "block") {
        content.style.display = "none";
    } else {
        content.style.display = "block";
    }
}
