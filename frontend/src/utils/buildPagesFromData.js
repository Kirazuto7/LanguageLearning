import VocabularyLesson from "../components/lessons/VocabularyLesson";
import BookPage from "../components/studybook/BookPage";
import ChapterPage from "../components/studybook/ChapterPage";

// Determines Page Component layout depending on the lesson type of the page
function getPageFromLessonType(page) {
    let pageContent;
    switch (page.lesson.type) {
        case 'vocabulary':
            pageContent = <VocabularyLesson lesson={page.lesson}/>;
            break;
        default:
            pageContent = <p>Unsupported lesson type: {page.lesson.type}</p>;
    }
    return pageContent;
}

// Helper function to process the Book Data into Component Pages
export function buildPagesFromBookData (bookData) {
    if(!bookData || !bookData.chapters) return [];

    let pages = [];
    
    // Loop through the book chapters
    bookData.chapters.forEach(chapter => {
        // Renders the first page of the chapter on the Chapter page
        const firstPage = chapter.pages[0];
        let firstPageContent = getPageFromLessonType(firstPage);
        pages.push(
            <ChapterPage
                key={`chapter-title-${chapter.id}`} 
                pageNumber={ firstPage.pageNumber } 
                isRightPage={ firstPage.pageNumber % 2 === 0 } 
                chapterNumber={chapter.chapterNumber} 
                chapterNativeTitle={chapter.nativeTitle}
                chapterTitle={chapter.title}
            >
                {firstPageContent}
            </ChapterPage>
        )

        // Loop through the page(s) data for the chapter starting from the 2nd page
        chapter.pages.slice(1).forEach(page => {
            // Determine the page content based on the type of lesson
            let pageContent = getPageFromLessonType(page);

            pages.push(
                <BookPage
                    key={`page-${page.id}`}
                    pageNumber={page.pageNumber}
                    isRightPage={page.pageNumber % 2 === 0}
                >
                    {pageContent}
                </BookPage>
            )
            
        });
    });
    return pages;
};

export function buildPagesFromChapters (chapters) {
    let pages = [];
    chapters.forEach(chapter => {
        const firstPage = chapter.pages[0];
        let firstPageContent = getPageFromLessonType(firstPage);
        pages.push(
            <ChapterPage
                key={`chapter-title-${chapter.id}`} 
                pageNumber={ firstPage.pageNumber } 
                isRightPage={ firstPage.pageNumber % 2 === 0 } 
                chapterNumber={chapter.chapterNumber} 
                chapterNativeTitle={chapter.nativeTitle}
                chapterTitle={chapter.title}
            >
                {firstPageContent}
            </ChapterPage>
        )

        // Loop through the page(s) data for the chapter
        chapter.pages.slice(1).forEach(page => {
            // Determine the page content based on the type of lesson
            let pageContent = getPageFromLessonType(page);

            pages.push(
                <BookPage
                    key={`page-${page.id}`}
                    pageNumber={page.pageNumber}
                    isRightPage={page.pageNumber % 2 === 0}
                >
                    {pageContent}
                </BookPage>
            )
        });
    });
    return pages;
};


// Example JSON Data
/*{
    "id": 1,
    "bookTitle": "Korean for Beginner learners",
    "difficulty": "Beginner",
    "language": "Korean",
    "chapters": [
        {
            "id": 1,
            "chapterNumber": 1,
            "title": "Food and Eating Out",
            "nativeTitle": "음식과 외식",
            "pages": [
                {
                    "id": 1,
                    "pageNumber": 1,
                    "lesson": {
                        "type": "vocabulary",
                        "id": 1,
                        "title": "Key Vocabulary for Food and Dining",
                        "items": [
                            {
                                "id": 1,
                                "word": "음식",
                                "translation": "food"
                            },
                            {
                                "id": 2,
                                "word": "요리",
                                "translation": "cooking/dish"
                            },
                            {
                                "id": 3,
                                "word": "식당",
                                "translation": "restaurant"
                            },
                            {
                                "id": 4,
                                "word": "메뉴",
                                "translation": "menu"
                            },
                            {
                                "id": 5,
                                "word": "주문하다",
                                "translation": "to order"
                            },
                            {
                                "id": 6,
                                "word": "치킨",
                                "translation": "chicken"
                            },
                            {
                                "id": 7,
                                "word": "비프",
                                "translation": "beef"
                            },
                            {
                                "id": 8,
                                "word": "피자",
                                "translation": "pizza"
                            },
                            {
                                "id": 9,
                                "word": "스파게티",
                                "translation": "spaghetti"
                            },
                            {
                                "id": 10,
                                "word": "음식점",
                                "translation": "eats/establishments"
                            },
                            {
                                "id": 11,
                                "word": "가격",
                                "translation": "price"
                            },
                            {
                                "id": 12,
                                "word": "비싸다",
                                "translation": "expensive"
                            },
                            {
                                "id": 13,
                                "word": "맛있다",
                                "translation": "tasty/delicious"
                            },
                            {
                                "id": 14,
                                "word": "배고프다",
                                "translation": "hungry"
                            },
                            {
                                "id": 15,
                                "word": "식사",
                                "translation": "meal"
                            },
                            {
                                "id": 16,
                                "word": "아침",
                                "translation": "breakfast"
                            },
                            {
                                "id": 17,
                                "word": "점심",
                                "translation": "lunch"
                            },
                            {
                                "id": 18,
                                "word": "저녁",
                                "translation": "dinner/evening meal"
                            }
                        ]
                    }
                }
            ]
        },
        {
            "id": 2,
            "chapterNumber": 2,
            "title": "Drinks and Beverages",
            "nativeTitle": "음료와 음료수",
            "pages": [
                {
                    "id": 2,
                    "pageNumber": 2,
                    "lesson": {
                        "type": "vocabulary",
                        "id": 2,
                        "title": "Key Vocabulary for Drinks",
                        "items": [
                            {
                                "id": 19,
                                "word": "물",
                                "translation": "water"
                            },
                            {
                                "id": 20,
                                "word": "소다",
                                "translation": "soda"
                            },
                            {
                                "id": 21,
                                "word": "차",
                                "translation": "tea"
                            },
                            {
                                "id": 22,
                                "word": "커피",
                                "translation": "coffee"
                            },
                            {
                                "id": 23,
                                "word": "주스",
                                "translation": "juice"
                            },
                            {
                                "id": 24,
                                "word": "알코올",
                                "translation": "alcohol"
                            },
                            {
                                "id": 25,
                                "word": "와인",
                                "translation": "wine"
                            },
                            {
                                "id": 26,
                                "word": "맥주",
                                "translation": "beer"
                            },
                            {
                                "id": 27,
                                "word": "사이다",
                                "translation": "cider"
                            },
                            {
                                "id": 28,
                                "word": "우유",
                                "translation": "milk"
                            },
                            {
                                "id": 29,
                                "word": "설탕",
                                "translation": "sugar"
                            }
                        ]
                    }
                }
            ]
        },
        {
            "id": 3,
            "chapterNumber": 3,
            "title": "학교 생활",
            "nativeTitle": "학교 생활",
            "pages": [
                {
                    "id": 3,
                    "pageNumber": 3,
                    "lesson": {
                        "type": "vocabulary",
                        "id": 3,
                        "title": "학교에서 배우는 중요한 것들",
                        "items": [
                            {
                                "id": 30,
                                "word": "교실",
                                "translation": "教室"
                            },
                            {
                                "id": 31,
                                "word": "선생님",
                                "translation": "교사"
                            },
                            {
                                "id": 32,
                                "word": "학생",
                                "translation": "학생"
                            },
                            {
                                "id": 33,
                                "word": "수업",
                                "translation": "수업"
                            },
                            {
                                "id": 34,
                                "word": "과목",
                                "translation": "교과목"
                            },
                            {
                                "id": 35,
                                "word": "숙제",
                                "translation": "과제"
                            },
                            {
                                "id": 36,
                                "word": "시험",
                                "translation": "시험"
                            },
                            {
                                "id": 37,
                                "word": "점수",
                                "translation": "점수"
                            },
                            {
                                "id": 38,
                                "word": "등급",
                                "translation": "등급"
                            },
                            {
                                "id": 39,
                                "word": "휴식시간",
                                "translation": "휴식 시간"
                            },
                            {
                                "id": 40,
                                "word": "점심시간",
                                "translation": "점심 시간"
                            }
                        ]
                    }
                }
            ]
        },
        {
            "id": 4,
            "chapterNumber": 4,
            "title": "여행",
            "nativeTitle": "여행",
            "pages": [
                {
                    "id": 4,
                    "pageNumber": 4,
                    "lesson": {
                        "type": "vocabulary",
                        "id": 4,
                        "title": "여행 필수품",
                        "items": [
                            {
                                "id": 41,
                                "word": "가방",
                                "translation": "bag"
                            },
                            {
                                "id": 42,
                                "word": "지갑",
                                "translation": "wallet"
                            },
                            {
                                "id": 43,
                                "word": "여권",
                                "translation": "passport"
                            },
                            {
                                "id": 44,
                                "word": "비행기 티켓",
                                "translation": "airline ticket"
                            },
                            {
                                "id": 45,
                                "word": "호텔 예약 확인서",
                                "translation": "hotel reservation confirmation"
                            },
                            {
                                "id": 46,
                                "word": "현금",
                                "translation": "cash"
                            },
                            {
                                "id": 47,
                                "word": "신용카드",
                                "translation": "credit card"
                            },
                            {
                                "id": 48,
                                "word": "여행자 수표",
                                "translation": "traveler's checks"
                            },
                            {
                                "id": 49,
                                "word": "여행 보험증서",
                                "translation": "travel insurance certificate"
                            },
                            {
                                "id": 50,
                                "word": "모자/선글라스/가벼운 재킷",
                                "translation": "hat/sunglasses/light jacket"
                            },
                            {
                                "id": 51,
                                "word": "편안한 신발",
                                "translation": "comfortable shoes"
                            }
                        ]
                    }
                }
            ]
        }
    ]
}*/