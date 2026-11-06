// レシピ詳細画面の人数計算
// 計算式：元の数量 × 表示したい人数 ÷ 登録時の人数

const panel = document.getElementById("servingsPanel");

<<<<<<< Updated upstream
if (panel !== null) {   // 人数が未登録なら、計算用の部品はHTMLにないので何もしない。
=======
// 人数が未登録なら、計算用の部品はHTMLにないので何もしない。
if (panel !== null) {
>>>>>>> Stashed changes
    const baseServings = Number(panel.dataset.baseServings);
    const select = document.getElementById("targetServings");
    const button = document.getElementById("calculateServings");
    const label = document.getElementById("displayedServings");
    const items = document.querySelectorAll(".detail-ingredients li"); //材料のli要素をまとめて取得する
    // 元の文章を保存しておく（2人前→4人前→2人前と変更してもずれないため）。
    const originalLines = [];
    for (const item of items) {
        originalLines.push(item.textContent);
    }

    button.addEventListener("click", function () {
        const targetServings = Number(select.value);
        if (baseServings < 1 || baseServings > 10 ||
            targetServings < 1 || targetServings > 10) {
            return;
        }

        for (let i = 0; i < items.length; i++) {
            items[i].textContent = calculateLine(originalLines[i], baseServings, targetServings);
        }
        label.textContent = targetServings + "人前";
    });
}

function calculateLine(line, baseServings, targetServings) {
    if (baseServings === targetServings) {
        return line;
    }

    // 数字が2か所以上ある材料は、間違った計算を防ぐため変えない。
    // 例：「卵 2個（約100g）」や「じゃがいも 2〜3個」
    const numbers = line.match(/\d+\/\d+|\d+(?:\.\d+)?/g);
    if (numbers === null || numbers.length !== 1) {
        return line;
    }

    // 「小さじ1/2」または「200g」「2本」などから数量を探す。
    const spoon = line.match(/(?:小さじ|大さじ)\s*(\d+\/\d+|\d+(?:\.\d+)?)/);
    const quantity = line.match(/(\d+\/\d+|\d+(?:\.\d+)?)\s*(?:kg|mg|g|mL|ml|cc|L|個|本|束|房|斤|枚|袋|片|切れ|かけ|欠片|丁|株|缶|パック|カップ|さじ)/);
    const match = spoon || quantity;
    if (match === null) {
        return line; // 「少々」「適量」などは変更しない。
    }

    const originalNumber = match[1];
    let amount = Number(originalNumber);
    if (originalNumber.includes("/")) {
        const parts = originalNumber.split("/");
        if (Number(parts[1]) === 0) {
            return line;
        }
        amount = Number(parts[0]) / Number(parts[1]);
    }

    const result = amount * targetServings / baseServings;
    if (!Number.isFinite(result)) {
        return line;
    }
    const rounded = Math.round(result * 100) / 100;
    const newNumber = Math.abs(result - rounded) > 0.00001 ? "約" + rounded : String(rounded);

    return line.replace(originalNumber, newNumber);
}
