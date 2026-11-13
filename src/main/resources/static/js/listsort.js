// =================================================================
// 機能：並び替え
// リスト画面の並び替えをJava側動作からJavascript側の動作に変更する。
// =================================================================

// 読み込み完了時に動作する。
document.addEventListener("DOMContentLoaded", () => {

    const sortOptions = document.getElementById("recipe-sort-options");
    const recipeGrid = document.querySelector(".recipe-grid");

    // 並び替えが未選択、もしくは、レシピグリッドが何もない場合は並び替えを実行しない。
    if (!sortOptions || !recipeGrid) return;

    // =================================================================
    // 機能：並び替えのロジック
    //   recipe配列を見る。
    //   ソートメソッドが2個取り出して比較。
    //   +-を見て入れ替える。これを繰り返す。
    // =================================================================
    const sortRecipes = (cards, sortType) => {

        // コピーを作成して並び替えた配列を返す。cardsでも動作するが安全性考慮でコピーを操作する。
        return [...cards].sort((a, b) => {

            // HTMLのdata属性から情報を取得
            const recipeA = a.dataset;
            const recipeB = b.dataset;

            switch (sortType) {

                // 更新日時：新しい順
                case "0": {
                    const dateA = Date.parse(recipeA.updatedAt);
                    const dateB = Date.parse(recipeB.updatedAt);

                    // 日付が存在しない場合は最後に表示
                    if (Number.isNaN(dateA)) return 1;
                    if (Number.isNaN(dateB)) return -1;

                    return dateB - dateA;
                }

                // 更新日時：古い順
                case "1": {
                    const dateA = Date.parse(recipeA.updatedAt);
                    const dateB = Date.parse(recipeB.updatedAt);

                    if (Number.isNaN(dateA)) return 1;
                    if (Number.isNaN(dateB)) return -1;

                    return dateA - dateB;
                }

                // 評価：高い順
                case "2":
                    return Number(recipeB.favoriteLevel) - Number(recipeA.favoriteLevel);

                // 評価：低い順
                case "3":
                    return Number(recipeA.favoriteLevel) - Number(recipeB.favoriteLevel);

                // 調理時間：長い順
                case "4":
                    return Number(recipeB.cookingTime) - Number(recipeA.cookingTime);

                // 調理時間：短い順
                case "5":
                    return Number(recipeA.cookingTime) - Number(recipeB.cookingTime);

                default:
                    return 0;
            }
        });
    };

    // =================================================================
    // 機能：並び替えのメソッド部分です。
    // =================================================================
    const updateRecipeList = (sortType) => {

        // 現在表示されているレシピカードを取得
        const cards = Array.from(
            recipeGrid.querySelectorAll(".recipe-card")
        );

        // 並び替えを実行
        const sortedCards = sortRecipes(cards, sortType);

        // 並び替えた順番でHTMLに追加
        sortedCards.forEach(card => {
            recipeGrid.appendChild(card);
        });
    };

    // =================================================================
    // 動作条件：sortselectがなにか変更されたとき。
    // updateRecipeListメソッドが動作する。
    // =================================================================
    // sortSelect.addEventListener("click", updateRecipeList);

    // html読み込み直後ならこちらが動作。
    // updateRecipeList();

    sortOptions.addEventListener("click", (event) => {

        // クリックされた要素がボタンか確認
        const button = event.target.closest("button[data-sort]");

        if (!button) return;

        // 並び替えの種類を取得
        const sortType = button.dataset.sort;

        // 並び替えを実行
        updateRecipeList(sortType);

        // 選択状態を更新
        sortOptions.querySelectorAll("button[data-sort]").forEach(btn => {

            const isSelected = btn === button;

            btn.classList.toggle("is-selected", isSelected);

            btn.setAttribute("aria-pressed", String(isSelected));
        });

        // 並び替え後にドロップダウンを閉じる
        document.getElementById("recipe-sort-dropdown").open = false;

    });

});


